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

package de.fh_zwickau.spr.crinc

import de.fh_zwickau.spr.crinc.domain.CriticalIncident
import de.fh_zwickau.spr.crinc.dto.ActorDto
import de.fh_zwickau.spr.crinc.dto.CriticalIncidentDto
import de.fh_zwickau.spr.crinc.dto.TextDto
import de.fh_zwickau.spr.crinc.repository.ActorRepository
import de.fh_zwickau.spr.crinc.repository.MediumRepository
import de.fh_zwickau.spr.crinc.service.CriticalIncidentService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

import javax.transaction.Transactional

@SpringApplicationConfiguration(classes = CriticalIncidentsApplication)
@Slf4j
class CICrudSpec extends Specification {

    @Autowired
    CriticalIncidentService criticalIncidentService
    @Autowired
    ActorRepository actorRepository
    @Autowired
    MediumRepository mediumRepository

    @Transactional
    def "CRUD Test"(){
        given: "Text dto, 2 Actor dtos, CI dto"
        TextDto textDto0 = new TextDto(story: 'story')
        textDto0.languageId = 1l
        ActorDto actorDto0 = new ActorDto(actorTypeId: 1l, originId: 2l)
        ActorDto actorDto1 = new ActorDto(actorTypeId: 2l, originId: 3l)
        CriticalIncidentDto criticalIncidentDto = new CriticalIncidentDto()

        when: "CIDto merges the creating author and one medium - createOrUpdate()"
        criticalIncidentDto.authorId = 1l
        criticalIncidentDto.mediums.add(textDto0)
        criticalIncidentService.createOrUpdate(criticalIncidentDto)

        then: "CIDto should got an id"
        criticalIncidentDto.id

        when: "Dto gets 2 Actors and all values and categories - createOrUpdate() - checkTheCI = getCIDto()"
        criticalIncidentDto.shortName = "short name"
        criticalIncidentDto.header = "head"
        criticalIncidentDto.verbal = true
        criticalIncidentDto.countryOfHappeningId = 1l
        criticalIncidentDto.fieldOfContactIds.add(1l)
        criticalIncidentDto.typeOfInteractionId = 1l
        criticalIncidentDto.hotspotIds.add(1l)
        criticalIncidentDto.actors.add(actorDto0)
        criticalIncidentDto.actors.add(actorDto1)
        criticalIncidentDto.ciOrigin = CriticalIncident.CiOrigin.OwnExperience
        criticalIncidentService.createOrUpdate(criticalIncidentDto)
        def checkTheCI = criticalIncidentService.getCriticalIncidentDto(criticalIncidentDto.id)

        then: "checkTheCi should got that"
        checkTheCI.mediums.size() == 1
        checkTheCI.shortName == "short name"
        checkTheCI.header == "head"
        checkTheCI.mediums[0].languageId == 1l
        checkTheCI.verbal == true
        checkTheCI.nonVerbal == false
        checkTheCI.paraverbal == false
        checkTheCI.proxematic == false
        checkTheCI.authorId == 1l
        checkTheCI.countryOfHappeningId == 1l
        checkTheCI.typeOfInteractionId == 1l
        checkTheCI.hotspotIds.size() == 1
        checkTheCI.mediums.size() == 1
        checkTheCI.actors.size() == 2
        checkTheCI.ciOrigin == CriticalIncident.CiOrigin.OwnExperience


        when: "Dto gets additional actor, additional fieldOfContact, additional hotspot - " +
                "createOrUpdate() - checkTheAdditions = getCIDto()"
        ActorDto actorDto2 = new ActorDto(actorTypeId: 3l, originId: 4l)
        criticalIncidentDto.actors.add(actorDto2)
        criticalIncidentDto.fieldOfContactIds.add(2l)
        criticalIncidentDto.hotspotIds.add(2l)
        criticalIncidentService.createOrUpdate(criticalIncidentDto)
        def checkTheAdditions = criticalIncidentService.getCriticalIncidentDto(criticalIncidentDto.id)

        then: "checkTheAdditions should got that"
        checkTheAdditions.actors.size() == 3
        checkTheAdditions.fieldOfContactIds.size() == 2
        checkTheAdditions.hotspotIds.size() == 2

        when: "Dto gets a changed header, changed country, changed typeOfInteraction, changed shortName, changed origin" +
                "createOreUpdate() - checkTheChanges = getCIDto()"
        criticalIncidentDto.header = "new value"
        criticalIncidentDto.shortName = "new short name"
        criticalIncidentDto.ciOrigin = CriticalIncident.CiOrigin.Captured
        criticalIncidentDto.countryOfHappeningId = 2l
        criticalIncidentDto.typeOfInteractionId = 2l
        criticalIncidentService.createOrUpdate(criticalIncidentDto)
        def checkTheChanges = criticalIncidentService.getCriticalIncidentDto(criticalIncidentDto.id)

        then: "checkTheChanges should got that"
        checkTheChanges.header == "new value"
        checkTheChanges.shortName == "new short name"
        checkTheChanges.ciOrigin == CriticalIncident.CiOrigin.Captured
        checkTheChanges.countryOfHappeningId == 2l
        checkTheChanges.typeOfInteractionId == 2l

        when: "Dto gets another medium1(text) and language of medium0 changes - createOrUpdate() - checkMediums = getCIDto()"
        TextDto textDto1 = new TextDto(story: 'another story')
        textDto1.languageId = 1l
        criticalIncidentDto.mediums[0].languageId = 2l
        criticalIncidentDto.mediums.add(textDto1)
        criticalIncidentService.createOrUpdate(criticalIncidentDto)
        def checkMediums = criticalIncidentService.getCriticalIncidentDto(criticalIncidentDto.id)

        then: "checkMediums should got that"
        checkMediums.mediums.size() == 2
        checkMediums.mediums[0].languageId == 2l

        when: "fieldOfContact and hotspot are removed from Dto - createOrUpdate() - checkTheSubstracitons = getCIDto()"
        criticalIncidentDto.fieldOfContactIds.remove(0)
        criticalIncidentDto.hotspotIds.remove(1)
        criticalIncidentService.createOrUpdate(criticalIncidentDto)
        def checkTheSubstractions = criticalIncidentService.getCriticalIncidentDto(criticalIncidentDto.id)

        then: "checkTheSubstractions should got that"
        checkTheSubstractions.fieldOfContactIds.size() == 1
        checkTheSubstractions.hotspotIds.size() == 1

        when: "medium is removed from Dto - createOrUpdate() - checkRemovedMedium = getCIDto()"
        def mediumToRemove = criticalIncidentDto.mediums[0].id
        criticalIncidentDto.mediums.remove(0)
        criticalIncidentService.createOrUpdate(criticalIncidentDto)
        def checkRemovedMedium = criticalIncidentService.getCriticalIncidentDto(criticalIncidentDto.id)

        then: "checkRemovedMedium should got that and repository Query to check DB entry is gone"
        checkRemovedMedium.mediums.size() == 1
        mediumRepository.findOne(mediumToRemove) == null

        when: "an actor is removed from Dto - actorsInDto - actorsInDB - createOrUpdate() - checkRemoveActor = getCIDto()"
        def actorsInDto = criticalIncidentDto.actors.size()
        def actorsInDb = actorRepository.count()
        criticalIncidentDto.actors.remove(0)
        criticalIncidentService.createOrUpdate(criticalIncidentDto)
        def checkRemoveActor = criticalIncidentService.getCriticalIncidentDto(criticalIncidentDto.id)

        then: "checkRemoveActor should got that - actors in Repository too"
        actorsInDto - 1 == checkRemoveActor.actors.size()
        actorsInDb - 1 == actorRepository.count()
        criticalIncidentDto.actors.size() > 1

        when: "actor has changed type or origin  - createOrUpdate()"
        def a0 = criticalIncidentDto.actors[0]
        def a1 = criticalIncidentDto.actors[1]
        a0.actorTypeId = 5l
        a1.originId = 5l
        criticalIncidentService.createOrUpdate(criticalIncidentDto)

        def a0c = actorRepository.findOne(a0.id)
        def a1c = actorRepository.findOne(a1.id)

        then: "actors have changed"
        a0c.type.one.id == 5l
        a1c.origin.one.id == 5l
    }
}
