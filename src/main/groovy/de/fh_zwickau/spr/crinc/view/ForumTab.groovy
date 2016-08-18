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

import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.Component
import de.geobe.util.vaadin.SubTree

import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F

@SpringComponent
@UIScope
class ForumTab extends SubTree{

    @Override
    Component build() {
        def c = vaadin."$C.hlayout"('Forum', [spacing: true, margin: false,
                                                              width  : '45em']) {
            "$C.vlayout"([spacing: true, margin: true]){
                //checkboxbar
                "$C.gridlayout"([columns: 4, rows: 1]){
                    "$F.checkbox"('best',[uikey: 'best', gridPosition: [0, 0], width: '5em', valueChangeListener: { bestBoxChanged() }])
                    "$F.checkbox"('most active',[uikey: 'best', gridPosition: [1, 0], width: '10em', valueChangeListener: { mostActiveBoxChanged() }])
                    "$F.checkbox"('mine',[uikey: 'best', gridPosition: [2, 0], width: '5em', valueChangeListener: { mineBoxChanged() }])
                    "$F.checkbox"('createEntry',[uikey: 'best', gridPosition: [3, 0], width: '5em', valueChangeListener: { createEntryBoxChanged() }])
                }
                //create entry
                "$C.gridlayout"([visible: true, columns: 3, rows: 1]){
                    "$F.label"([uikey: 'entryTtLbl', gridPosition: [0,0]])
                    "$F.textarea"([uikey: 'entryTxt', width: '30em', gridPosition: [1, 0]])
                    "$F.button"('send',[uikey: 'sendEntryBtn', gridPosition: [2, 0], clickListener: { sendEntryBtnClick() }])
                }
                //forum entries
                "$C.gridlayout"([visible: true, columns: 2, rows: 2]){
                    "$F.table"([uikey: 'entriesTbl', gridPosition: [0, 0, 1, 0]])
                    "$F.button"('<',[uikey: 'leftBtn', gridPosition: [0, 1], clickListener: { leftBtnClick() }])
                    "$F.button"('>',[uikey: 'rightBtn', gridPosition: [1, 1], clickListener: { rightBtnClick() }])
                }
                //forum entry
                "$C.gridlayout"([visible: true, columns: 2, rows: 4]){
                    "$F.label"([uikey: 'authorLbl', gridPosition: [0, 0]])
                    "$F.label"([uikey: 'messageLbl', gridPosition: [0, 1]])
                    "$F.table"([uikey: 'repliesTbl', gridPosition: [0, 2, 1, 2]])
                    //reply
                    "$C.gridlayout"([spacing: false, margin: true, columns: 2, rows: 1, gridPosition: [0, 3, 1, 3]]){
                        "$F.textarea"([uikey: 'replyTxt', gridPosition: [0, 0]])
                        "$F.button"('reply',[uikey: 'replyBtn', gridPosition: [1, 0], clickListener: { replyBtnClick() }])
                    }
                }
            }
        }
        init()
        c
    }

    private init() {
//        uiComponents = vaadin.uiComponents
//        allCis()
//        tags = uiComponents['browse.tags']
//        ciHeader = uiComponents['browse.ciHeader']
//        ciHeader.value = "<b>${cIDtos.header[ciCount]}</b>"
//        ciStory = uiComponents['browse.ciStory']
//        ciStory.value = "<b>${cIDtos[ciCount].mediums[0].story}</b>"
//        updateBtn = uiComponents['browse.updateBtn']
//        updateBtn.setStyleName(Reindeer.BUTTON_LINK)
    }
}
