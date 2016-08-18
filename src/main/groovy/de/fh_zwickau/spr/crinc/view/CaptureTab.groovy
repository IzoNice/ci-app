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
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F

@Slf4j
@SpringComponent
@UIScope
class CaptureTab extends SubTree {
    @Autowired
    private CriticalIncidentService criticalIncidentService
    @Autowired
    private CaptureCategoriesView captureCategoriesView
    @Autowired
    private BrowseTab browseTab
    @Autowired
    private LoginTab loginTab
//    @Autowired
//    private ForumTab forumTab

    private Component categoriesRoot
    private boolean categoriesVisible = false
    private Label headerFieldLabel, storyAreaLabel
    private TextField headerField
    private TextArea storyArea
    private Button saveButton, updateButton, escButton
    private CheckBox categoriesBox
    private HorizontalLayout categoriesLayout

    private long updateDtoId, updateDtoAuthorId

    @Override
    Component build() {
        categoriesRoot = captureCategoriesView.buildSubtree(vaadin, 'categories.')
        def c = vaadin."$C.hlayout"('erfassen', [uikey: 'categoriesLayout', spacing: true, margin: false,
                                                 width  : '45em']) {
            "$C.vlayout"([spacing: true, margin: true]) {
                "$F.label"([uikey: 'headerFieldLabel', contentMode: ContentMode.HTML])
                "$F.text"([uikey: 'headerField', width: '40em'])
                "$F.label"([uikey: 'storyAreaLabel', contentMode: ContentMode.HTML])
                "$F.textarea"([uikey: 'storyArea', width: '40em'])
                "$F.checkbox"('categories', [uikey              : 'categoriesBox', margin: false, value: false,
                                             valueChangeListener: { categoriesBoxCheck() }])
                "$F.subtree"(categoriesRoot, [uikey: 'categoriesArea', visible: false, gridPosition: [0, 0]])
                "$C.hlayout"([uikey : 'buttonArea', spacing: true,
                              margin: false, alignment: Alignment.BOTTOM_LEFT]) {
                    "$F.button"('Speichern', [uikey: 'saveButton', clickListener: { saveButtonClick() }])
                    "$F.button"('Bearbeitung Speichern', [uikey: 'updateButton',
                                                          visible: false, clickListener: { updateButtonClick() }])
                    "$F.button"('Abbrechen', [uikey: 'escButton', clickListener: { escButtonClick() }])
                }
            }
        }
        init()
        c
    }

    private init() {
        uiComponents = vaadin.uiComponents
        browseTab.captureTab = this
        categoriesLayout = uiComponents['capture.categoriesLayout']
        saveButton = uiComponents['capture.saveButton']
        updateButton = uiComponents['capture.updateButton']
        escButton = uiComponents['capture.escButton']
        categoriesBox = uiComponents['capture.categoriesBox']
        headerFieldLabel = uiComponents['capture.headerFieldLabel']
        headerFieldLabel.value = "<b>header</b>"
        headerField = uiComponents['capture.headerField']
        storyAreaLabel = uiComponents['capture.storyAreaLabel']
        storyAreaLabel.value = "<b>story</b>"
        storyArea = uiComponents['capture.storyArea']
    }

    private categoriesBoxCheck() {
        if (categoriesVisible) {
            categoriesVisible = false
            categoriesRoot.visible = false
//            researchButton.caption = 'for Research'
        } else {
            categoriesVisible = true
            categoriesRoot.visible = true
//            researchButton.caption = 'hide Research'
        }
    }

    private saveButtonClick() {
        CriticalIncidentDto ciDto = new CriticalIncidentDto()
        ciDto.header = headerField.value
        ciDto.authorId = loginTab.loggedIn.id
        TextDto textDto = new TextDto(story: storyArea.value)
        ciDto.mediums << textDto
        captureCategoriesView.populateDto(ciDto)
        criticalIncidentService.createOrUpdate(ciDto)

        browseTab.allCis()
        resetFields()
    }

    private updateButtonClick() {
        CriticalIncidentDto ciDto = new CriticalIncidentDto()
        ciDto.authorId = updateDtoAuthorId
        ciDto.id = updateDtoId
        ciDto.header = headerField.value
        TextDto textDto = new TextDto(story: storyArea.value)
        ciDto.mediums << textDto
        captureCategoriesView.populateDto(ciDto)
        criticalIncidentService.createOrUpdate(ciDto)
        saveButton.visible = true
        updateButton.visible = false
        resetFields()
    }

    private escButtonClick() {
        resetFields()
    }

    private resetFields() {
        headerField.value = ''
        storyArea.value = ''
        captureCategoriesView.resetFields()
    }

    public updateCi(CriticalIncidentDto cIDto){
        resetFields()
        updateDtoId = cIDto.id
        updateDtoAuthorId = cIDto.authorId
        headerField.value = cIDto.header
        storyArea.value = cIDto.mediums[0].story
        captureCategoriesView.updateCiCategories(cIDto)
        saveButton.visible = false
        updateButton.visible = true
    }
}
