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
import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.Label
import com.vaadin.ui.TabSheet
import com.vaadin.ui.themes.Reindeer
import de.fh_zwickau.spr.crinc.dto.CriticalIncidentDto
import de.fh_zwickau.spr.crinc.dto.ReferenceDataDto
import de.fh_zwickau.spr.crinc.service.CriticalIncidentService
import de.fh_zwickau.spr.crinc.service.ReferenceDataService
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
    @Autowired
    private ReferenceDataService referenceDataService

    CaptureTab captureTab

    private ReferenceDataDto referenceDataDto

    public getReferenceDataDto() { referenceDataDto }

    private Component categoriesRoot, browseRoot, captureRoot
    private Label ciStory, ciHeader
    private Label tags
    private Button updateBtn, leftBtn, rightBtn
    private TabSheet toptab
    CriticalIncidentDto cIDto = new CriticalIncidentDto()
    List<CriticalIncidentDto> cIDtos = []
    private int ciCount = 0

    @Override
    Component build() {
//        categoriesRoot = categoriesTab.buildSubtree(vaadin, 'categories.')
        def c = vaadin."$C.hlayout"('ansehen', [spacing: true, margin: false,
                                                width  : '45em']) {
            "$C.vlayout"([spacing: true, margin: true]) {
                "$C.gridlayout"(
                        [spacing: false, margin: false, columns: 4, rows: 1, visible: false]) {
                    "$F.checkbox"('best',
                            [uikey              : 'best', gridPosition: [0, 0], width: '5em',
                             valueChangeListener: { bestBoxChanged() }])
                    "$F.checkbox"('most active',
                            [uikey              : 'mostActive', gridPosition: [1, 0], width: '10em',
                             valueChangeListener: { mostActiveBoxChanged() }])
                    "$F.checkbox"('mine',
                            [uikey              : 'mine', gridPosition: [2, 0], width: '5em',
                             valueChangeListener: { mineBoxChanged() }])
                    "$F.checkbox"('categories',
                            [uikey              : 'categories', gridPosition: [3, 0], width: '5em',
                             valueChangeListener: { categoriesBoxChanged() }])
                }
                //categories Filter
//                "$F.subtree"(categoriesRoot, [uikey: 'categoriesArea', visible: false,
// gridPosition: [0, 0]])
//                "$F.button"('Filter',
//                        [uikey: 'filterBtn', gridPosition: [0, 1], visible: false,
// clickListener: { filterBtnClick() }])

                //ciList
                "$C.gridlayout"([uikey: 'ciList', visible: false, columns: 2, rows: 2]) {
                    "$F.table"([uikey: 'cis', gridPosition: [0, 0, 1, 0]])
                    "$F.button"('<', [uikey: 'leftBtn', gridPosition: [0, 1], clickListener: {
                        ciListLeftBtnClick()
                    }])
                    "$F.button"('>', [uikey: 'rightBtn', gridPosition: [1, 1], clickListener: {
                        ciListRightBtnClick()
                    }])
                }
//                //ciUpdate
//                "$C.gridlayout"([uikey: 'ciUpdate', visible: false])
                //ciRead
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
                "$F.button"('bearbeiten',
                        [uikey        : 'updateBtn',
                         clickListener: { updateBtnClick() }])
                "$F.label"([uikey      : 'tags', width: '40em',
                            contentMode: ContentMode.HTML])
            }
        }
        init()
        c
    }

    private init() {
        referenceDataDto = referenceDataService.getReferenceData()
        uiComponents = vaadin.uiComponents
        allCis()
        tags = uiComponents['browse.tags']
        ciHeader = uiComponents['browse.ciHeader']
        ciHeader.value = "<b>${cIDtos.header[ciCount]}</b>"
        ciStory = uiComponents['browse.ciStory']
        ciStory.value = "<b>${cIDtos[ciCount].mediums[0].story}</b>"
        updateBtn = uiComponents['browse.updateBtn']
        tags.value = "<b>tags: ${dtoString(cIDtos[ciCount])}</b>"
        updateBtn.setStyleName(Reindeer.BUTTON_LINK)
    }

    private updateBtnClick() {
        // nur beim ersten mal initialisieren mit ?:
        toptab = toptab ?: uiComponents['toptab']
        browseRoot = browseRoot ?: uiComponents['browseTab']
        captureRoot = captureRoot ?: uiComponents['captureTab']
        cIDto = cIDtos[ciCount]
        toptab.selectedTab = captureRoot
        captureTab.updateCi(cIDto)
    }

    public void allCis() {
        cIDtos = criticalIncidentService.getAllCriticalIncidentDtos()
    }

    public void refreshCi(CriticalIncidentDto ciDto) {
        def id = ciDto.id
        for (int i = 0; i < cIDtos.size(); i++) {
            if(cIDtos[i].id == id) {
                cIDtos[i] = ciDto
                if(ciCount == i) {
                    updater(ciCount)
                }
                break
            }
        }
    }

    private String dtoString(CriticalIncidentDto cIDto) {
        String dtoStr = ''
        dtoStr += "<b>Origin:</b> ${cIDto.ciOrigin}<br> "
        if (cIDto.verbal)
            dtoStr += "<b>verbal:</b> ${cIDto.verbal}<br> "
        if (cIDto.nonVerbal)
            dtoStr += "<b>nonVerbal:</b> ${cIDto.nonVerbal}<br> "
        if (cIDto.paraverbal)
            dtoStr += "<b>paraverbal:</b> ${cIDto.paraverbal}<br> "
        if (cIDto.proxematic)
            dtoStr += "<b>proxematisch:</b> ${cIDto.proxematic}<br> "
        dtoStr += "<b>Autor:</b> ${cIDto.authorId}<br> "
        dtoStr += "<b>Land des Geschehens:</b> ${cIDto.countryOfHappeningId} <br>"
        dtoStr += "<b>Kontaktfelder:</b> ${cIDto.fieldOfContactIds}<br> "
        dtoStr += "<b>Interaktionstyp:</b> ${cIDto.typeOfInteractionId}<br> "
        dtoStr += "<b>Hotspots:</b> ${cIDto.hotspotIds}<br> "
//        dtoStr += "<b>mediums:</b> ${cIDto.mediums}<br> "
        if (cIDto.actors) {
            dtoStr += "<b>actors:</b> "
            cIDto.actors.each { actor ->
                dtoStr += "${actor}<br>"
            }
        }
//        String mediums = ''

        dtoStr
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

    private updater(int counter) {
        ciHeader.value = "<b>${cIDtos.header[counter]}</b>"
        ciStory.value = "<b>${cIDtos[counter].mediums[0].story}</b>"
        tags.value = "<b>tags: ${dtoString(cIDtos[counter])}</b>"
    }
}