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
import de.fh_zwickau.spr.crinc.dto.CriticalIncidentDto
import de.fh_zwickau.spr.crinc.dto.TextDto
import de.fh_zwickau.spr.crinc.service.CriticalIncidentService
import de.geobe.util.vaadin.SubTree
import org.springframework.beans.factory.annotation.Autowired

import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F

@SpringComponent
@UIScope
class CaptureTab extends SubTree {
    @Autowired
    private CriticalIncidentService criticalIncidentService
    @Autowired
    private ResearchTab researchTab
    @Autowired
    private BrowseTab browseTab

    private Component researchRoot
    private boolean researchVisible = false
    private Label headerFieldLabel, storyFieldLabel
    private TextField headerField
    private TextArea storyField
    private Button saveButton, escButton, researchButton


    @Override
    Component build() {
        researchRoot = researchTab.buildSubtree(vaadin, 'research.')
        def c = vaadin."$C.hlayout"('erfassen', [spacing: true, margin: false,
                                                 width  : '45em']) {
            "$C.vlayout"([spacing: true, margin: true]) {
                "$C.gridlayout"(
                        [spacing: false, margin: false,
                         columns: 1, rows: 2]) {
                    "$F.label"([uikey: 'headerFieldLabel', contentMode: ContentMode.HTML,
                                gridPosition: [0, 0]])
                    "$F.text"([uikey: 'headerField', width: '40em',
                               gridPosition: [0, 1]])
                }
                "$C.gridlayout"(
                        [spacing: false, margin: false,
                         columns: 1, rows: 2]) {
                    "$F.label"([uikey: 'storyFieldLabel', contentMode: ContentMode.HTML,
                                                          gridPosition: [0, 0]])
                    "$F.textarea"([uikey: 'storyField', width: '40em',
                                                        gridPosition: [0, 1]])
                }
                "$F.button"('for Research',
                        [uikey        : 'researchButton', alignment: Alignment.MIDDLE_RIGHT,
                         clickListener: { researchButtonClick() }])
                "$F.subtree"(researchRoot, [uikey: 'researchArea', visible: false])
                "$C.hlayout"([uikey : 'buttonArea', spacing: true,
                              margin: false, alignment: Alignment.BOTTOM_LEFT]) {
                    "$F.button"('Speichern', [uikey: 'saveButton', clickListener: { saveButtonClick() }])
                    "$F.button"('Abbrechen',
                            [uikey: 'escButton', clickListener: { escButtonClick() }])
                }
            }
        }
        init()
        c
    }

    private init() {
        uiComponents = vaadin.uiComponents
        saveButton = uiComponents['capture.saveButton']
        escButton = uiComponents['capture.escButton']
        researchButton = uiComponents['capture.researchButton']
        headerFieldLabel = uiComponents['capture.headerFieldLabel']
        headerFieldLabel.value = "<b>header</b>"
        headerField = uiComponents['capture.headerField']
        storyFieldLabel = uiComponents['capture.storyFieldLabel']
        storyFieldLabel.value = "<b>story</b>"
        storyField = uiComponents['capture.storyField']
    }

    private researchButtonClick() {
        if (researchVisible) {
            researchVisible = false
            researchRoot.visible = false
            researchButton.caption = 'for Research'
        } else {
            researchVisible = true
            researchRoot.visible = true
            researchButton.caption = 'hide Research'
        }
    }

    private saveButtonClick() {
        CriticalIncidentDto criticalIncidentDto = new CriticalIncidentDto()
        criticalIncidentDto.header = headerField.value
        criticalIncidentDto.authorId = 1l
        TextDto textDto = new TextDto(story: storyField.value)
        criticalIncidentDto.mediums << textDto
        researchTab.populateDto(criticalIncidentDto)
        criticalIncidentService.createOrUpdate(criticalIncidentDto)

        browseTab.allCis()
        resetFields()
    }

    private escButtonClick() {
        resetFields()
    }

    private resetFields(){
        headerField.value = ''
        storyField.value = ''
        researchTab.resetFields()
    }
}
