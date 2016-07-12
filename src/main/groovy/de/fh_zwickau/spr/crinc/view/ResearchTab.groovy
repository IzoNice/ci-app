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

package de.fh_zwickau.spr.crinc.view

import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import de.fh_zwickau.spr.crinc.dto.*
import de.fh_zwickau.spr.crinc.service.ReferenceDataService
import de.fh_zwickau.spr.crinc.view.helper.ActorData
import de.fh_zwickau.spr.crinc.view.helper.ReferenceData
import de.geobe.util.vaadin.SubTree
import org.springframework.beans.factory.annotation.Autowired

import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F

@SpringComponent
@UIScope
class ResearchTab extends SubTree {
    public static final String ACTORS = 'Actors'
    @Autowired
    private ReferenceDataService referenceDataService

    private OptionGroup storyType
    private ComboBox languageOfNarration
    private ComboBox typeOfInteraction, medialMediated, written
    private ComboBox countryOfHappening
    private ComboBox actorsOrigin, actorType
    private Button addActor
    private Table actors
    private CheckBox verbal, nonVerbal, paraverbal, proxematic
    private ComboBox fieldOfContact
    private Button addFieldOfContact
    private Table fieldsOfContact
    private ComboBox hotspot

    private Label storyTypeLbl, languageOfNarrationLbl, typeOfInteractionLbl,
                  countryOfHappeningLbl, actorsLbl,
                  levelOfCommunicationLbl, fieldOfContactLbl, hotspotLbl


    private ReferenceDataDto referenceDataDto

    @Override
    Component build() {
        def c = vaadin."$C.csslayout"([spacing: false, margin: false]) {

            "$C.gridlayout"(
                    [spacing: false, margin: true,
                     columns: 1, rows: 2]) {
                "$F.label"([uikey      : 'storyTypeLbl',
                            contentMode: ContentMode.HTML, gridPosition: [0, 0]
                ])
                "$F.optiongroup"(
                        [uikey       : 'storyType',
//                         alignment   : Alignment.TOP_RIGHT,
                         gridPosition: [0, 1], visible: true])
            }

            "$C.gridlayout"([spacing: false, margin: true,
                             columns: 1, rows: 2,]) {
                "$F.label"([uikey      : 'languageOfNarrationLbl',
                            contentMode: ContentMode.HTML, gridPosition: [0, 0]
                ])
                "$F.combo"([uikey  : 'languageOfNarration', gridPosition: [0, 1],
                            visible: true]) {}
            }

            "$C.gridlayout"([spacing: false, margin: true,
                             columns: 1, rows: 4]) {
                "$F.label"([uikey      : 'typeOfInteractionLbl',
                            contentMode: ContentMode.HTML, gridPosition: [0, 0]
                ])
                "$F.combo"([uikey              : 'typeOfInteraction',
//                         alignment   : Alignment.TOP_RIGHT,
                            gridPosition       : [0, 1], visible: true])
                "$F.combo"('Medial vermittelt',
                        [uikey       : 'mediaMediated',
//                         alignment   : Alignment.TOP_RIGHT,
                         gridPosition: [0, 2], visible: false])
                "$F.combo"('schriftlich',
                        [uikey       : 'written',
//                         alignment   : Alignment.TOP_RIGHT,
                         gridPosition: [0, 3], visible: false])
            }

            "$C.gridlayout"([spacing: false, margin: true,
                             columns: 1, rows: 2,]) {
                "$F.label"([uikey      : 'countryOfHappeningLbl',
                            contentMode: ContentMode.HTML, gridPosition: [0, 0]
                ])
                "$F.combo"([uikey  : 'countryOfHappening', gridPosition: [0, 1],
                            visible: true])
            }

            "$C.gridlayout"([spacing: false, margin: true,
                             columns: 3, rows: 3,]) {
                "$F.label"([uikey      : 'actorsLbl',
                            contentMode: ContentMode.HTML, gridPosition: [0, 0, 2, 0]
                ])
                "$F.combo"(
                        [uikey  : 'actorsOrigin', gridPosition: [0, 1],
                         visible: true])
                "$F.combo"(
                        [uikey  : 'actorType', gridPosition: [1, 1],
                         visible: true])
                "$F.button"("add",
                        [uikey  : 'addActor', gridPosition: [2, 1],
                         visible: true, clickListener: { addActor() }])
                "$F.table"(
                        [uikey     : 'actors', width: '100%',
                         selectable: true, valueChangeListener: { removeActor(it) },
                         height    : '200px', gridPosition: [0, 2, 2, 2]])
            }
            "$C.gridlayout"([spacing: false, margin: true,
                             columns: 2, rows: 3]) {
                "$F.label"([uikey      : 'fieldOfContactLbl',
                            contentMode: ContentMode.HTML, gridPosition: [0, 0, 1, 0]
                ])
                "$F.combo"(
                        [uikey  : 'fieldOfContact', gridPosition: [0, 1],
                         visible: true])
                "$F.button"("add",
                        [uikey  : 'addFieldOfContact', gridPosition: [1, 1],
                         visible: false, clickListener: { addContactField() }])
                "$F.table"(
                        [uikey     : 'fieldsOfContact', width: '100%',
                         selectable: true, valueChangeListener: { removeContactField(it) },
                         height    : '200px', gridPosition: [0, 2, 1, 2],
                         visible: false])
            }
            "$C.gridlayout"([spacing: false, margin: true,
                             columns: 2, rows: 3]) {
                "$F.label"([uikey      : 'hotspotLbl',
                            contentMode: ContentMode.HTML, gridPosition: [0, 0]
                ])
                "$F.combo"(
                        [uikey  : 'hotspot', gridPosition: [0, 1],
                         visible: true])
            }
            "$C.gridlayout"([spacing: false,
                             margin : true,
                             columns: 1,
                             rows   : 5]) {
                "$F.label"([uikey      : 'levelOfCommunicationLbl',
                            contentMode: ContentMode.HTML, gridPosition: [0, 0]
                ])
                "$F.checkbox"('verbal',
                        [uikey       : 'verbal', margin: true,
                         gridPosition: [0, 1], value: false])
                "$F.checkbox"('non-verbal',
                        [uikey       : 'nonVerbal', margin: true,
                         gridPosition: [0, 2], value: false])
                "$F.checkbox"('paraverbal',
                        [uikey       : 'paraverbal', margin: true,
                         gridPosition: [0, 3], value: false])
                "$F.checkbox"('proxematisch',
                        [uikey       : 'proxematic', margin: true,
                         gridPosition: [0, 4], value: false])
            }
        }
        init()
        c
    }

    private init() {
        referenceDataDto = referenceDataService.getReferenceData()
        uiComponents = vaadin.uiComponents
        storyType = uiComponents['capture.research.storyType']
        storyTypeLbl = uiComponents['capture.research.storyTypeLbl']
        storyTypeLbl.setValue('<b>Text Typ<b/>')
        referenceDataDto.storyType.each {
            storyType.addItem(it)
        }

        languageOfNarration = uiComponents['capture.research.languageOfNarration']
        languageOfNarrationLbl = uiComponents['capture.research.languageOfNarrationLbl']
        languageOfNarrationLbl.setValue('<b>Sprache der Erzählung<b/>')
        referenceDataDto.language.each { k, v ->
            languageOfNarration.addItem(k)
            languageOfNarration.setItemCaption(k, v)
        }

        typeOfInteraction = uiComponents['capture.research.typeOfInteraction']
        typeOfInteractionLbl = uiComponents['capture.research.typeOfInteractionLbl']
        typeOfInteractionLbl.setValue('<b>Interaktionsart<b/>')
        medialMediated = uiComponents['capture.research.medialMediated']
        written = uiComponents['capture.research.written']
        referenceDataDto.typeOfInteraction.each { k, v ->
            typeOfInteraction.addItem(k)
            def caption = "${v[0]}${v[1] ? ', ' + v[1] : ''}".toString()
            typeOfInteraction.setItemCaption(k, caption)
        }

        countryOfHappening = uiComponents['capture.research.countryOfHappening']
        countryOfHappeningLbl = uiComponents['capture.research.countryOfHappeningLbl']
        countryOfHappeningLbl.setValue('<b>Wo geschieht der CI?<b/>')
        referenceDataDto.country.each { k, v ->
            countryOfHappening.addItem(k)
            countryOfHappening.setItemCaption(k, v)
        }


        actorsOrigin = uiComponents['capture.research.actorsOrigin']
        actorsLbl = uiComponents['capture.research.actorsLbl']
        actorsLbl.setValue('<b>Wer sind die Akteure?<b/>')
        referenceDataDto.actorsOrigin.each { k, v ->
            actorsOrigin.addItem(k)
            actorsOrigin.setItemCaption(k, v)
        }

        actorType = uiComponents['capture.research.actorType']
        referenceDataDto.actorType.each { k, v ->
            actorType.addItem(k)
            actorType.setItemCaption(k, v)
        }
        addActor = uiComponents['capture.research.addActor']
        actors = uiComponents['capture.research.actors']
        actors.addContainerProperty(ACTORS, ActorData.class, null)



        fieldOfContact = uiComponents['capture.research.fieldOfContact']
        fieldOfContactLbl = uiComponents['capture.research.fieldOfContactLbl']
        fieldOfContactLbl.setValue('<b>Welches ist das Kontaktfeld?<b/>')
        referenceDataDto.fieldOfContact.each { k, v ->
            fieldOfContact.addItem(k)
            fieldOfContact.setItemCaption(k, v)
        }
        addFieldOfContact = uiComponents['capture.research.addFieldOfContact']
        fieldsOfContact = uiComponents['capture.research.fieldsOfContact']
        fieldsOfContact.addContainerProperty("Fields", ReferenceData.class, null)

        hotspot = uiComponents['capture.research.hotspot']
        referenceDataDto.hotspot.each { k, v ->
            hotspot.addItem(k)
            hotspot.setItemCaption(k, v)
        }
        hotspotLbl = uiComponents['capture.research.hotspotLbl']
        hotspotLbl.setValue('<br><b>Hotspot<b/>')

        levelOfCommunicationLbl = uiComponents['capture.research.levelOfCommunicationLbl']
        levelOfCommunicationLbl.setValue('<b>Welche Kommunikationsebene wird benannt?<b/>')
        verbal = uiComponents['capture.research.verbal']
        nonVerbal = uiComponents['capture.research.nonVerbal']
        paraverbal = uiComponents['capture.research.paraverbal']
        proxematic = uiComponents['capture.research.proxematic']
    }

    private addActor() {
        def actorTypeId = actorType.value
        def actorTypeCaption = actorType.getItemCaption(actorTypeId)
        def originId = actorsOrigin.value
        def originCaption = actorsOrigin.getItemCaption(originId)
        ActorData actorData = new ActorData(originId: originId, origin: originCaption,
                actorTypeId: actorTypeId, actorType: actorTypeCaption)
        actors.addItem([actorData].toArray(), null)
    }

    private removeActor(it) {
        actors.removeItem(it.source.getValue())
    }

    private addContactField() {
        def contactId = fieldOfContact.value
        def contactCaption = fieldOfContact.getItemCaption(contactId)
        ReferenceData contactData = new ReferenceData(id: contactId, caption: contactCaption)
        fieldsOfContact.addItem([contactData].toArray(), null)
    }

    private removeContactField(it) {
        fieldsOfContact.removeItem(it.source.getValue())
    }

    public populateDto(CriticalIncidentDto criticalIncidentDto) {
        criticalIncidentDto.countryOfHappeningId = countryOfHappening.value
        criticalIncidentDto.fieldOfContactIds = fieldOfContact.value
        criticalIncidentDto.typeOfInteractionId = typeOfInteraction.value
        criticalIncidentDto.hotspotIds = hotspot.value
        criticalIncidentDto.verbal = verbal.value
        criticalIncidentDto.nonVerbal = nonVerbal.value
        criticalIncidentDto.paraverbal = paraverbal.value
        criticalIncidentDto.proxematic = proxematic.value
        MediumDto mediumDto = criticalIncidentDto?.mediums[0]
        mediumDto.languageId = languageOfNarration.value
        if(mediumDto instanceof TextDto) {
            mediumDto.storyType = storyType.value
        }
        def actorIds = actors.itemIds
        actorIds.each { itemId ->
            ActorData actorData = actors.getItem(itemId).getItemProperty(ACTORS).value
            ActorDto actorDto = new ActorDto(id: actorData.id,
                    actorTypeId: actorData.actorTypeId,
                    originId: actorData.originId)
            criticalIncidentDto.actors << actorDto
        }
        resetFields()
    }

    private resetFields(){
        storyType.value = null
        languageOfNarration.value = null
        typeOfInteraction.value = null
        countryOfHappening.value = null
        actorsOrigin.value = null
        actorType.value = null
        actors.removeAllItems()
        verbal.setValue(false)
        nonVerbal.setValue(false)
        paraverbal.setValue(false)
        proxematic.setValue(false)
        fieldOfContact.value = null
        fieldsOfContact.removeAllItems()
        hotspot.value = null
    }
}
