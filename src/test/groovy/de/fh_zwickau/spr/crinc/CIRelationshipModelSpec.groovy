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

import de.fh_zwickau.spr.crinc.domain.Actor
import de.fh_zwickau.spr.crinc.domain.CriticalIncident
import de.fh_zwickau.spr.crinc.domain.Text
import de.fh_zwickau.spr.crinc.domain.User
import de.fh_zwickau.spr.crinc.repository.ActorTypeRepository
import de.fh_zwickau.spr.crinc.repository.CountryRepository
import de.fh_zwickau.spr.crinc.repository.CriticalIncidentRepository
import de.fh_zwickau.spr.crinc.repository.FieldOfContactRepository
import de.fh_zwickau.spr.crinc.repository.HotspotRepository
import de.fh_zwickau.spr.crinc.repository.LanguageRepository
import de.fh_zwickau.spr.crinc.repository.OriginRepository
import de.fh_zwickau.spr.crinc.repository.TypeOfInteractionRepository
import de.fh_zwickau.spr.crinc.repository.UserRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

import javax.transaction.Transactional

@SpringApplicationConfiguration(classes = CriticalIncidentsApplication)
@Slf4j
class CIRelationshipModelSpec extends Specification {

    @Autowired
    private CriticalIncidentRepository criticalIncidentRepository
    @Autowired
    private UserRepository userRepository
    @Autowired
    private LanguageRepository languageRepository
    @Autowired
    private TypeOfInteractionRepository typeOfInteractionRepository
    @Autowired
    private ActorTypeRepository actorTypeRepository
    @Autowired
    private OriginRepository originRepository
    @Autowired
    private CountryRepository countryRepository
    @Autowired
    private HotspotRepository hotspotRepository
    @Autowired
    private FieldOfContactRepository fieldOfContactRepository

    @Transactional
    def "A User can create n CIs. CI has toOne and toMany relations. N is substituted by 2"() {

        given: "1 User, 2 CIs, 3 txts, 1 language, 1 typeOfInteraction, 2 actors, 2 actorTypes, " +
                "2 origins, 2 country, 2 hotspots, 2 contactFields"
        def usr = new User([name: 'TestAuthor', password: 'pw'])

        def ci0 = new CriticalIncident(
                [shortName: 'ttt', header: 'Test CI 0', ciOrigin: CriticalIncident.CiOrigin.OwnExperience, verbal: true]
        )
        def ci1 = new CriticalIncident(
                [shortName: 'ttt', header: 'Test CI 0', ciOrigin: CriticalIncident.CiOrigin.OwnExperience, nonVerbal: true]
        )

        def ciTxt0 = new Text([
                story: "Test Story 0"
        ])
        def ciTxt1 = new Text([
                story: "Test Story 1", storyType: Text.StoryType.PRIMARY_FIRST_PERSON
        ])
        def ciTxt2 = new Text([
                story: "Test Story 2", storyType: Text.StoryType.PRIMARY_FIRST_PERSON
        ])
        def langDE = languageRepository.findByLabelLike('Deut%')[0]
        def toi = typeOfInteractionRepository.getOne(2l)
        def actor0 = new Actor()
        def actor1 = new Actor()
        def actorType0 = actorTypeRepository.findByLabelLike('Ki%')[0]
        def actorType1 = actorTypeRepository.findByLabelLike('ano%')[0]
        def origin0 = originRepository.findByLabelLike('Sow%')[0]
        def origin1 = originRepository.findByLabelLike('Irl%')[0]
        def countryDE = countryRepository.findByNameLike('Deu')[0]
        def hots0 = hotspotRepository.findByLabelLike('Begr%')[0]
        def hots1 = hotspotRepository.findByLabelLike('Hum%')[0]
        def contactField0 = fieldOfContactRepository.findByLabelLike('Freun%')[0]
        def contactField1 = fieldOfContactRepository.findByLabelLike('Disc%')[0]

        when: "the user becomes an author, the CIs get related and flushed"
        userRepository.saveAndFlush(usr)
        def author = userRepository.findByNameLike('Test%')[0]

        ci0.author.add(author)
        ci0.mediums.add(ciTxt0)
        criticalIncidentRepository.saveAndFlush(ci0)

        ciTxt1.language.add(langDE)
        ciTxt2.language.add(langDE)

        ci1.author.add(author)
        ci1.mediums.add(ciTxt1)
        ci1.mediums.add(ciTxt2)
        ci1.typeOfInteraction.add(toi)
        actor0.origin.add(origin0)
        actor0.type.add(actorType0)
        actor1.origin.add(origin1)
        actor1.type.add(actorType1)
        ci1.actors.add(actor0)
        ci1.actors.add(actor1)
        ci1.countryOfHappening.add(countryDE)
        ci1.hotspots.add(hots0)
        ci1.hotspots.add(hots1)
        ci1.fieldsOfContact.add(contactField0)
        ci1.fieldsOfContact.add(contactField1)
        criticalIncidentRepository.saveAndFlush(ci1)

        def testAuthor = userRepository.findByNameLike('Test%')[0]

        then: "the testAuthor will have 2 CIs and they got all set relations"

        //CI 1
        testAuthor.criticalIncidents.all.size() == 2
        testAuthor.criticalIncidents.all[0].mediums.all.size() == 1
        testAuthor.criticalIncidents.all[0].actors.all.size() == 0
        testAuthor.criticalIncidents.all[0].ciOrigin == CriticalIncident.CiOrigin.OwnExperience
        testAuthor.criticalIncidents.all[0].typeOfInteraction.one == null
        testAuthor.criticalIncidents.all[0].actors.all.size() == 0
        testAuthor.criticalIncidents.all[0].countryOfHappening.one == null
        testAuthor.criticalIncidents.all[0].hotspots.all.size() == 0
        testAuthor.criticalIncidents.all[0].fieldsOfContact.all.size() == 0

        //CI 2
        testAuthor.criticalIncidents.all[1].mediums.all.size() == 2
        testAuthor.criticalIncidents.all[1].mediums.all[0].language.one.label == langDE.label
        testAuthor.criticalIncidents.all[1].mediums.all[1].language.one == langDE
        testAuthor.criticalIncidents.all[1].actors.all.size() == 2
        testAuthor.criticalIncidents.all[1].actors.all[0].origin == actor0.origin
        testAuthor.criticalIncidents.all[1].actors.all[1].origin == actor1.origin
        testAuthor.criticalIncidents.all[1].ciOrigin == CriticalIncident.CiOrigin.OwnExperience
        testAuthor.criticalIncidents.all[1].typeOfInteraction.one == toi
        testAuthor.criticalIncidents.all[1].countryOfHappening.one == countryDE
        testAuthor.criticalIncidents.all[1].hotspots.all.size() == 2
        testAuthor.criticalIncidents.all[1].hotspots.all[0] == hots0
        testAuthor.criticalIncidents.all[1].hotspots.all[1] == hots1
        testAuthor.criticalIncidents.all[1].fieldsOfContact.all.size() == 2
        testAuthor.criticalIncidents.all[1].fieldsOfContact.all[0] == contactField0
        testAuthor.criticalIncidents.all[1].fieldsOfContact.all[1] == contactField1
    }
}
