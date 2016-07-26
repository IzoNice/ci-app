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

    List<CriticalIncident> cis = []
    private int ciCount = 0
    CriticalIncidentDto criticalIncidentDto = new CriticalIncidentDto()

    @Override
    Component build() {
        def c = vaadin."$C.hlayout"('ansehen', [spacing: true, margin: false,
                                                width  : '45em']) {
            "$C.vlayout"([spacing: true, margin: true, width: '40em']) {
                "$C.hlayout"([spacing: false, margin: false]) {
                    "$F.button"('<',
                            [uikey        : 'backButton',
                             clickListener: { backButtonClick(it) }])
                    "$F.button"('>',
                            [uikey        : 'nextButton',
                             clickListener: { nextButtonClick(it) }])
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
//        allCis()
        tags = uiComponents['browse.tags']
        ciHeader = uiComponents['browse.ciHeader']
        ciHeader.value = "<b>${cis.header[ciCount]}</b>"
        ciStory = uiComponents['browse.ciStory']

    }

    public void allCis() {
//        cis = criticalIncidentService.getAllCriticalIncidents()
    }

    private String tagToString(CriticalIncident criticalIncident) {
        String tagString = ''
        if (criticalIncident.mediums.all) {
            String mediums = ''
            criticalIncident.mediums.all.each { medium ->
                mediums += "${medium}, "
            }
            tagString += "<b>Text Typ:</b> ${mediums}, "
        }

        if (criticalIncident.typeOfInteraction.one)
            tagString += "<b>Interaktionsart:</b> ${criticalIncident.typeOfInteraction.one.label} "
        if (criticalIncident.countryOfHappening)
            tagString += "<b>Land:</b> ${criticalIncident.countryOfHappening.name}, "
        if (criticalIncident.actors.all) {
            String actors = ''
            criticalIncident.actors.all.each { actor ->
                actors += "${actor.type.one.label} Herkunft: ${actor.origin.one.label}, "
            }
            tagString += "<b>Akteure:</b> ${actors} "
        }
        if (criticalIncident.verbal || criticalIncident.nonVerbal || criticalIncident.paraverbal
                || criticalIncident.proxematic) {
            String levelOfCommunicationsString = ''
            if (criticalIncident.verbal)
                levelOfCommunicationsString += "verbal, "
            if (criticalIncident.nonVerbal)
                levelOfCommunicationsString += "non-verbal, "
            if (criticalIncident.paraverbal)
                levelOfCommunicationsString += "paraverbal, "
            if (criticalIncident.proxematic)
                levelOfCommunicationsString += "proxematisch, "
            tagString += "<b>Kommunikationsebene:</b> ${levelOfCommunicationsString}"
        }
        if (criticalIncident.fieldsOfContact.all) {
            String fieldsOfContact = ''
            criticalIncident.fieldsOfContact.all.each { fieldOfContact ->
                fieldsOfContact += "${fieldOfContact.label}, "
            }
            tagString += "<b>Kontaktfeld:</b> ${fieldsOfContact} "
        }
        if (criticalIncident.hotspot.one?.label) {
            tagString += "<b>Hotspot:</b> ${criticalIncident.hotspot.one?.label}"
        }

        tagString
    }

    private nextButtonClick(def it) {
        if (ciCount == cis.size() - 1)
            ciCount = -1
        ciHeader.value = "<b>${cis.header[++ciCount]}</b>"
    }

    private backButtonClick(def it) {
        if (ciCount.abs() == cis.size())
            ciCount = 0
        ciHeader.value = "<b>${cis.header[--ciCount]}</b>"
    }
}