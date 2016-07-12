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
    def "An Author can create two CIs, one of them with two Mediums and sets value to all relations. \"To n\" relations are tested by 2 inputs"() {

        given: "a User, two CIs, three txts, a language, a typeOfInteraction, two actors, two actorTypes, " +
                "two origins, a country, two hotspots, two contactFields"
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

        when: "the user becomes an author, the CIs get related and saveAndFlushed"

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

        then: "something very cool should happen"
        author.criticalIncidents.all.size() == 2
        ci0.mediums.all.size() == 1
        ci0.actors.all.size() == 0
        ci0.ciOrigin == CriticalIncident.CiOrigin.OwnExperience
        ci0.typeOfInteraction.one == null
        ci0.actors.all.size() == 0
        ci0.countryOfHappening.one == null
        ci0.hotspots.all.size() == 0
        ci0.fieldsOfContact.all.size() == 0

        ci1.mediums.all.size() == 2
        ci1.actors.all.size() == 2
        ci1.ciOrigin == CriticalIncident.CiOrigin.OwnExperience
        ci1.typeOfInteraction.one == toi
        ci1.actors.all.size() == 2
        ci1.countryOfHappening.one == countryDE
        ci1.hotspots.all.size() == 2
        ci1.fieldsOfContact.all.size() == 2
    }
}
