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

import de.fh_zwickau.spr.crinc.domain.Text
import de.fh_zwickau.spr.crinc.dto.ReferenceDataDto
import de.fh_zwickau.spr.crinc.repository.ActorTypeRepository
import de.fh_zwickau.spr.crinc.repository.CountryRepository
import de.fh_zwickau.spr.crinc.repository.FieldOfContactRepository
import de.fh_zwickau.spr.crinc.repository.HotspotRepository
import de.fh_zwickau.spr.crinc.repository.LanguageRepository
import de.fh_zwickau.spr.crinc.repository.OriginRepository
import de.fh_zwickau.spr.crinc.repository.TypeOfInteractionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReferenceDataService {
    @Autowired
    private CountryRepository countryRepository
    @Autowired
    private OriginRepository originRepository
    @Autowired
    private ActorTypeRepository actorTypeRepository
    @Autowired
    private FieldOfContactRepository fieldOfContactRepository
    @Autowired
    private TypeOfInteractionRepository typeOfInteractionRepository
    @Autowired
    private HotspotRepository hotspotRepository
    @Autowired
    private LanguageRepository languageRepository

    ReferenceDataDto getReferenceData() {
        def dto = new ReferenceDataDto()
        actorTypeRepository.findAll().each { actorType ->
            dto.actorType[actorType.id] = actorType.label
        }
        fieldOfContactRepository.findAll().each { foc ->
            dto.fieldOfContact[foc.id] = foc.label
        }
        hotspotRepository.findAll().each { hotspot ->
            dto.hotspot[hotspot.id] = hotspot.label
        }
        languageRepository.findAll().each { language ->
            dto.language[language.id] = language.label
        }
        countryRepository.findAll().each { country ->
            dto.country[country.id] = country.name
        }
        originRepository.findAll().each { origin ->
            dto.actorsOrigin[origin.id] = origin.label
        }
        typeOfInteractionRepository.findAll().each { typeOfInteraction ->
            dto.typeOfInteraction[typeOfInteraction.id] =
                    [typeOfInteraction.itype, typeOfInteraction.label]
        }
        dto.storyType = Text.StoryType.values()
        dto
    }
}
