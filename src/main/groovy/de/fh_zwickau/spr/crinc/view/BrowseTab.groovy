/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016.  Christoph Beier. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.fh_zwickau.spr.crinc.view

import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import de.fh_zwickau.spr.crinc.domain.CriticalIncident
import de.fh_zwickau.spr.crinc.dto.CriticalIncidentDto
import de.fh_zwickau.spr.crinc.service.CriticalIncidentService
import de.geobe.util.vaadin.SubTree
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F


@SpringComponent
@UIScope
@Slf4j
class BrowseTab extends SubTree {
    @Autowired
    private CriticalIncidentService criticalIncidentService

    private Label ciStory, ciHeader
    private Label tags
    CriticalIncidentDto cIDto = new CriticalIncidentDto()
    List<CriticalIncidentDto> cIDtos = []
    private int ciCount = 0

    @Override
    Component build() {
        def c = vaadin."$C.hlayout"('ansehen', [spacing: true, margin: false,
                                                width  : '45em']) {
            "$C.vlayout"([spacing: true, margin: true, width: '40em']) {
                "$C.hlayout"([spacing: false, margin: false]) {
                    "$F.button"('<',
                            [uikey        : 'backButton',
                             clickListener: { lastCiButtonClick(it) }])
                    "$F.button"('>',
                            [uikey        : 'nextButton',
                             clickListener: { nextCiButtonClick(it) }])
                }
                "$F.label"([uikey      : 'ciHeader', width: '40em',
                            contentMode: ContentMode.HTML])
                "$F.label"([uikey      : 'ciStory', width: '40em',
                            contentMode: ContentMode.HTML])
                "$F.label"([uikey      : 'tags', width: '40em',
                            contentMode: ContentMode.HTML])
            }
        }
        init()
        c
    }

    private init() {
        uiComponents = vaadin.uiComponents
        allCis()
        tags = uiComponents['browse.tags']
        ciHeader = uiComponents['browse.ciHeader']
        ciHeader.value = "<b>${cIDtos.header[ciCount]}</b>"
        ciStory = uiComponents['browse.ciStory']
        ciStory.value = "<b>${cIDtos[ciCount].mediums[0].story}</b>"
    }

    public void allCis() {
        cIDtos = criticalIncidentService.getAllCriticalIncidentDtos()
    }

    private String dtoString(CriticalIncidentDto cIDto) {
        String dtoStr = ''
        dtoStr += "<b>Origin:</b> ${cIDto.ciOrigin}<br> "
        dtoStr += "<b>verbal:</b> ${cIDto.verbal}<br> "
        dtoStr += "<b>nonVerbal:</b> ${cIDto.nonVerbal}<br> "
        dtoStr += "<b>paraverbal:</b> ${cIDto.paraverbal}<br> "
        dtoStr += "<b>proxematisch:</b> ${cIDto.proxematic}<br> "
        dtoStr += "<b>Autor:</b> ${cIDto.authorId}<br> "
        dtoStr += "<b>Land des Geschehens:</b> ${cIDto.countryOfHappeningId} <br>"
        dtoStr += "<b>Kontaktfelder:</b> ${cIDto.fieldOfContactIds}<br> "
        dtoStr += "<b>Interaktionstyp:</b> ${cIDto.typeOfInteractionId}<br> "
        dtoStr += "<b>Hotspots:</b> ${cIDto.hotspotIds}<br> "
        dtoStr += "<b>mediums:</b> ${cIDto.mediums}<br> "
        dtoStr += "<b>actors:</b> ${cIDto.actors} <br>"
        String mediums = ''

        dtoStr
    }

    private String tagToString(CriticalIncident cI) {
        String tagString = ''
        if (cI.mediums.all) {
            String mediums = ''
            cI.mediums.all.each { medium ->
                mediums += "${medium}, "
            }
            tagString += "<b>Text Typ:</b> ${mediums}, "
        }

        if (cI.typeOfInteraction.one)
            tagString += "<b>Interaktionsart:</b> ${cI.typeOfInteraction.one.label} "
        if (cI.countryOfHappening)
            tagString += "<b>Land:</b> ${cI.countryOfHappening.name}, "
        if (cI.actors.all) {
            String actors = ''
            cI.actors.all.each { actor ->
                actors += "${actor.type.one.label} Herkunft: ${actor.origin.one.label}, "
            }
            tagString += "<b>Akteure:</b> ${actors} "
        }
        if (cI.verbal || cI.nonVerbal || cI.paraverbal
                || cI.proxematic) {
            String levelOfCommunicationsString = ''
            if (cI.verbal)
                levelOfCommunicationsString += "verbal, "
            if (cI.nonVerbal)
                levelOfCommunicationsString += "non-verbal, "
            if (cI.paraverbal)
                levelOfCommunicationsString += "paraverbal, "
            if (cI.proxematic)
                levelOfCommunicationsString += "proxematisch, "
            tagString += "<b>Kommunikationsebene:</b> ${levelOfCommunicationsString}"
        }
        if (cI.fieldsOfContact.all) {
            String fieldsOfContact = ''
            cI.fieldsOfContact.all.each { fieldOfContact ->
                fieldsOfContact += "${fieldOfContact.label}, "
            }
            tagString += "<b>Kontaktfeld:</b> ${fieldsOfContact} "
        }
        if (cI.hotspot.one?.label) {
            tagString += "<b>Hotspot:</b> ${cI.hotspot.one?.label}"
        }

        tagString
    }

    private nextCiButtonClick(def it) {
        if (ciCount == cIDtos.size() - 1)
            ciCount = -1
        updater(++ciCount)
    }

    private lastCiButtonClick(def it) {
        if (ciCount.abs() == cIDtos.size())
            ciCount = 0
        updater(--ciCount)
    }

//    private nextMediumButtonClick(def it) {
//        if (mediumCount == cIDtos.size() - 1)
//            mediumCount = -1
//        updater(++mediumCount)
//    }
//
//    private lastMediumButtonClick(def it) {
//        if (mediumCount.abs() == cIDtos[ciCount].mediums.size())
//            mediumCount = 0
////        updater(--mediumCount)
//    }

    private updater(int counter){
        ciHeader.value = "<b>${cIDtos.header[counter]}</b>"
        ciStory.value = "<b>${cIDtos[counter].mediums[0].story}</b>"
        tags.value = "<b>tags: ${dtoString(cIDtos[counter])}</b>"
    }
}