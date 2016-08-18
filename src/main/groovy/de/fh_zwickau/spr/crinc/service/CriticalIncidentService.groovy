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
    public User login(String uName, def password){
        User user = userRepository.findByName(uName)
        if (user){
            if (user.password == password){
                return user
            }
        }else {
            return new User()
        }
    }

    @Transactional
    public List<CriticalIncidentDto> getAllCriticalIncidentDtos(){
        def cIs = criticalIncidentRepository.findAll()
        CriticalIncidentDto cIDto = new CriticalIncidentDto()
        List<CriticalIncidentDto> cIDtos = []
        cIs.each {cI ->
            cIDto = createCiDto(cI)
            cIDtos << cIDto
        }
        cIDtos
    }

    private CriticalIncidentDto createCiDto(CriticalIncident cI) {
        CriticalIncidentDto cIDto = new CriticalIncidentDto()
        cIDto.id = cI.id
        cIDto.shortName = cI.shortName
        cIDto.header = cI.header
        cIDto.verbal = cI.verbal
        cIDto.nonVerbal = cI.nonVerbal
        cIDto.paraverbal = cI.paraverbal
        cIDto.proxematic = cI.proxematic

//        log.info("$cI.author")
//        log.info("$cI.author.one")
//        log.info("$cI.author.one.criticalIncidents.one.author.one")
        if (cI.author) {
            cIDto.authorId = cI.author.one.id
        }
        if (cI.typeOfInteraction.one){
            cIDto.typeOfInteractionId = cI.typeOfInteraction.one.id
        }
        if (cI.fieldsOfContact.all){
            cI.fieldsOfContact.all.each { fieldofContact ->
                cIDto.fieldOfContactIds.add(fieldofContact.id)
            }
        }
        cIDto.ciOrigin = cI.ciOrigin
        if (cI.countryOfHappening.one){
            cIDto.countryOfHappeningId = cI.countryOfHappening.one.id
        }
        if (cI.hotspots.all){
            cI.hotspots.all.each { hotspot ->
                cIDto.hotspotIds.add(hotspot.id)
            }
        }
        cI.mediums.all.each { Medium medium ->
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
            if (medium.language.one){
                mediumDto.languageId = medium.language.one.id
            }
            cIDto.mediums << mediumDto
        }
        cI.actors.all.each { Actor actor ->
            ActorDto actorDto = new ActorDto()
            actorDto.id = actor.id
            actorDto.actorTypeId = actor.type.one.id
            actorDto.originId = actor.origin.one.id
            cIDto.actors << actorDto
        }
        cIDto
    }

    @Transactional
    public CriticalIncidentDto getCriticalIncidentDto(Long id) {
        CriticalIncident cI = criticalIncidentRepository.getOne(id)
        CriticalIncidentDto cIDto = new CriticalIncidentDto()
        if (cI) {
            cIDto = createCiDto(cI)
        }
        cIDto
    }

    @Transactional
    public CriticalIncidentDto createOrUpdate(CriticalIncidentDto cIDto) {
        CriticalIncident cI

        // A CI needs a medium to be a CI
        if (cIDto.mediums) {
            if (cIDto.id) {
                cI = criticalIncidentRepository.getOne(cIDto.id)
                if (!cI) {
                    return new CriticalIncidentDto()
                }
            } else {
                cI = new CriticalIncident()
            }
            cI.shortName = cIDto.shortName
            cI.header = cIDto.header
            cI.ciOrigin = cIDto.ciOrigin
            cI.verbal = cIDto.verbal
            cI.nonVerbal = cIDto.nonVerbal
            cI.paraverbal = cIDto.paraverbal
            cI.proxematic = cIDto.proxematic

            if (cIDto.authorId) {
                cI.author = userRepository.getOne(cIDto.authorId)
            }
            if (cIDto.typeOfInteractionId) {
                cI.typeOfInteraction = typeOfInteractionRepository.
                        getOne(cIDto.typeOfInteractionId)
            }
            if (cIDto.countryOfHappeningId) {
                cI.countryOfHappening = countryRepository.
                        getOne(cIDto.countryOfHappeningId)
            }

            if (cIDto.fieldOfContactIds) {
                cI.fieldsOfContact.removeAll()
                cIDto.fieldOfContactIds.each { fieldOfContactId ->
                    cI.fieldsOfContact.
                            add(fieldOfContactRepository.getOne(fieldOfContactId))
                }
            }
            if (cIDto.hotspotIds) {
                cI.hotspots.removeAll()
                cIDto.hotspotIds.each { hotspotId ->
                    cI.hotspots.add(hotspotRepository.getOne(hotspotId))
                }
            }

            // remove mediums from db that are no longer in dto
            if (cIDto.id && cI.id) {
                def idsInDb = cI.mediums.all.id
                def idsInDto = cIDto.mediums.id
                def idsToDelete = idsInDb - idsInDto
                def mediumsToDelete = mediumRepository.findAll(idsToDelete)
                mediumsToDelete.each { medium ->
                    cI.mediums.remove(medium)
                    mediumRepository.delete(medium)
                }
            }
            cIDto.mediums.each { mediumDto ->
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
                    cI.mediums.add(medium)
                }
                if (medium instanceof Text && mediumDto instanceof TextDto) {
                    medium.story = mediumDto.story
                    medium.storyType = mediumDto.storyType
                }
                if (mediumDto.languageId)
                    medium.language.add(languageRepository.findOne(mediumDto.languageId))
            }
            if (cIDto.actors) {
                // remove actors from db that are no longer in dto
                if (cIDto.id && cI.id) {
                    def idsInDb = cI.actors.all.id
                    def idsInDto = cIDto.actors.id
                    def idsToDelete = idsInDb - idsInDto
                    def objectsToDelete = actorRepository.findAll(idsToDelete)
                    objectsToDelete.each { actor ->
                        cI.actors.remove(actor)
                    }
                    actorRepository.deleteInBatch(objectsToDelete)
                }
                cIDto.actors.each { actorDto ->
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
                        cI.actors.add(actor)
                    }
                }
            }


            cI = criticalIncidentRepository.saveAndFlush(cI)
            cIDto.id = cI.id
            cIDto.actors.clear()
            cI.actors.all.each { actor ->
                cIDto.actors.add(
                        new ActorDto(id: actor.id, actorTypeId: actor.type.one.id,
                                originId: actor.origin.one.id))
            }
        }
//        log.info("cIDto.id = ${cIDto.id}")
        cIDto
    }
}