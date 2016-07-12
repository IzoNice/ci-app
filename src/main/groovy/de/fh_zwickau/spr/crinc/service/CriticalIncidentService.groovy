/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2016.  Christoph Beier. All rights reserved.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package de.fh_zwickau.spr.crinc.service

import de.fh_zwickau.spr.crinc.domain.*
import de.fh_zwickau.spr.crinc.dto.*
import de.fh_zwickau.spr.crinc.repository.*
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Service
@Slf4j
class CriticalIncidentService {
    @Autowired
    private CriticalIncidentRepository criticalIncidentRepository
    @Autowired
    private ActorRepository actorRepository
    @Autowired
    private ActorTypeRepository actorTypeRepository
    @Autowired
    private MediumRepository mediumRepository
    @Autowired
    private TypeOfInteractionRepository typeOfInteractionRepository
    @Autowired
    private FieldOfContactRepository fieldOfContactRepository
    @Autowired
    private HotspotRepository hotspotRepository
    @Autowired
    private LanguageRepository languageRepository
    @Autowired
    private CountryRepository countryRepository
    @Autowired
    private OriginRepository originRepository
    @Autowired
    private UserRepository userRepository


    @Transactional
    public CriticalIncidentDto getCriticalIncidentDto(Long id) {
        CriticalIncident criticalIncident = criticalIncidentRepository.getOne(id)
        CriticalIncidentDto criticalIncidentDto = new CriticalIncidentDto()
        if (criticalIncident) {
            criticalIncidentDto.id = criticalIncident.id
            criticalIncidentDto.shortName = criticalIncident.shortName
            criticalIncidentDto.header = criticalIncident.header
            criticalIncidentDto.verbal = criticalIncident.verbal
            criticalIncidentDto.nonVerbal = criticalIncident.nonVerbal
            criticalIncidentDto.paraverbal = criticalIncident.paraverbal
            criticalIncidentDto.proxematic = criticalIncident.proxematic
            criticalIncidentDto.authorId = criticalIncident.author.one.id
            if (criticalIncident.typeOfInteraction.one)
                criticalIncidentDto.typeOfInteractionId = criticalIncident.typeOfInteraction.one.id
            criticalIncident.fieldsOfContact.all.each { fieldofContact ->
                criticalIncidentDto.fieldOfContactIds.add(fieldofContact.id)
            }
            criticalIncidentDto.ciOrigin = criticalIncident.ciOrigin
            if (criticalIncident.countryOfHappening.one)
                criticalIncidentDto.countryOfHappeningId = criticalIncident.countryOfHappening.one.id
            criticalIncident.hotspots.all.each { hotspot ->
                criticalIncidentDto.hotspotIds.add(hotspot.id)
            }
//            criticalIncidentDto.hotspotIds = criticalIncident.hotspot.one.id
            criticalIncident.mediums.all.each { Medium medium ->
                MediumDto mediumDto
                if (medium instanceof Text) {
                    mediumDto = new TextDto()
                    mediumDto.story = medium.story
                    mediumDto.storyType = medium.storyType
                } else if (medium instanceof MultiMedia) {
                    mediumDto = new MultiMediaDto()
                    mediumDto.fileName = medium.fileName
                    mediumDto.type = medium.type
                } else {
                    mediumDto = new MediumDto()
                }
                mediumDto.id = medium.id
                mediumDto.languageId = medium.language.one.id
                criticalIncidentDto.mediums << mediumDto
            }
            criticalIncident.actors.all.each { Actor actor ->
                ActorDto actorDto = new ActorDto()
                actorDto.id = actor.id
                actorDto.actorTypeId = actor.type.one.id
                actorDto.originId = actor.origin.one.id
                criticalIncidentDto.actors << actorDto
            }
        }
        criticalIncidentDto
    }

    @Transactional
    public CriticalIncidentDto createOrUpdate(CriticalIncidentDto criticalIncidentDto) {
        CriticalIncident criticalIncident
        if (criticalIncidentDto.id) {
            criticalIncident = criticalIncidentRepository.getOne(criticalIncidentDto.id)
            if (!criticalIncident) {
                return new CriticalIncidentDto()
            }
        } else {
            criticalIncident = new CriticalIncident()
        }
        criticalIncident.shortName = criticalIncidentDto.shortName
        criticalIncident.header = criticalIncidentDto.header
        criticalIncident.ciOrigin = criticalIncidentDto.ciOrigin
        criticalIncident.verbal = criticalIncidentDto.verbal
        criticalIncident.nonVerbal = criticalIncidentDto.nonVerbal
        criticalIncident.paraverbal = criticalIncidentDto.paraverbal
        criticalIncident.proxematic = criticalIncidentDto.proxematic
        if (criticalIncidentDto.authorId) {
            criticalIncident.author = userRepository.getOne(criticalIncidentDto.authorId)
        }
        if (criticalIncidentDto.typeOfInteractionId) {
            criticalIncident.typeOfInteraction = typeOfInteractionRepository.
                    getOne(criticalIncidentDto.typeOfInteractionId)
        }
        // A CI needs a medium to be a CI
        if (criticalIncidentDto.mediums) {
            // remove mediums from db that are no longer in dto
            if (criticalIncidentDto.id && criticalIncident.id) {
                def idsInDb = criticalIncident.mediums.all.id
                def idsInDto = criticalIncidentDto.mediums.id
                def idsToDelete = idsInDb - idsInDto
                def objectsToDelete = mediumRepository.findAll(idsToDelete)
                objectsToDelete.each { medium ->
                    criticalIncident.mediums.remove(medium)
                    mediumRepository.delete(medium)
                }
            }
            criticalIncidentDto.mediums.each { mediumDto ->
                Medium medium = null
                if (mediumDto.id)
                    medium = mediumRepository.getOne(mediumDto.id)
                else {
                    if (mediumDto instanceof TextDto) {
                        medium = new Text()
                    } else {
                        medium = new MultiMedia()
                    }
                    medium = mediumRepository.saveAndFlush(medium)
                    mediumDto.id = medium.id
                    criticalIncident.mediums.add(medium)
                }
                if (medium instanceof Text && mediumDto instanceof TextDto) {
                    medium.story = mediumDto.story
                    medium.storyType = mediumDto.storyType
                }
                if (mediumDto.languageId)
                    medium.language.add(languageRepository.findOne(mediumDto.languageId))
            }
            if (criticalIncidentDto.actors) {
                // remove actors from db that are no longer in dto
                if (criticalIncidentDto.id && criticalIncident.id) {
                    def idsInDb = criticalIncident.actors.all.id
                    def idsInDto = criticalIncidentDto.actors.id
                    def idsToDelete = idsInDb - idsInDto
                    def objectsToDelete = actorRepository.findAll(idsToDelete)
                    objectsToDelete.each { actor ->
                        criticalIncident.actors.remove(actor)
                    }
                    actorRepository.deleteInBatch(objectsToDelete)
                }
                criticalIncidentDto.actors.each { actorDto ->
                    Actor actor = null
                    // update existing actors
                    if (actorDto.id) {
                        actor = actorRepository.getOne(actorDto.id)
                        if (actor.type.one.id != actorDto.actorTypeId || actor.origin.one.id !=
                                actorDto.originId) {
                            actor.type.add(actorTypeRepository.getOne(actorDto.actorTypeId))
                            actor.origin.add(originRepository.getOne(actorDto.originId))
                            actorRepository.saveAndFlush(actor)
                        }
                    }
                    // add new actors to db
                    if (!actor) {
                        actor = new Actor()
                        actorRepository.saveAndFlush(actor)
                        actor.type.add(actorTypeRepository.getOne(actorDto.actorTypeId))
                        actor.origin.add(originRepository.getOne(actorDto.originId))
                        criticalIncident.actors.add(actor)
                    }
                }
            }
            if (criticalIncidentDto.countryOfHappeningId) {
                criticalIncident.countryOfHappening = countryRepository.
                        getOne(criticalIncidentDto.countryOfHappeningId)
            }
            if (criticalIncidentDto.fieldOfContactIds) {
                criticalIncident.fieldsOfContact.removeAll()
                criticalIncidentDto.fieldOfContactIds.each { fieldOfContactId ->
                    criticalIncident.fieldsOfContact.
                            add(fieldOfContactRepository.getOne(fieldOfContactId))
                }
            }
            if (criticalIncidentDto.hotspotIds) {
                criticalIncident.hotspots.removeAll()
                criticalIncidentDto.hotspotIds.each { hotspotId ->
                    criticalIncident.hotspots.add(hotspotRepository.getOne(hotspotId))
                }
            }

            criticalIncident = criticalIncidentRepository.saveAndFlush(criticalIncident)
            criticalIncidentDto.id = criticalIncident.id
            criticalIncidentDto.actors.clear()
            criticalIncident.actors.all.each { actor ->
                criticalIncidentDto.actors.add(
                        new ActorDto(id: actor.id, actorTypeId: actor.type.one.id,
                                originId: actor.origin.one.id))
            }
        }
        criticalIncidentDto
    }
}