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

import com.vaadin.event.ShortcutAction
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import de.fh_zwickau.spr.crinc.dto.UserDto
import de.fh_zwickau.spr.crinc.service.UserService
import de.geobe.util.vaadin.SubTree
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F

@Slf4j
@SpringComponent
@UIScope
class LoginTab extends SubTree {
    @Autowired
    private UserService userService

    private Label loginFailLabel, loggedOutLabel
    private FormLayout form
    private TextField userName
    private PasswordField password
    private Button login
    private VerticalLayout loginLayout, logoutLayout
    private TabSheet toptab

    private Component browseRoot, captureRoot, forumRoot

    private UserDto loggedIn
    public getLoggedIn() { loggedIn }


    @Override
    Component build() {
        def c = vaadin."$C.vlayout"('login', [uikey    : 'loginLogoutLayout', sizeUndefined: null,
                      alignment: Alignment.MIDDLE_CENTER]) {
            "$C.vlayout"([uikey    : 'loginLayout', sizeUndefined: null,
                                   alignment: Alignment.MIDDLE_CENTER]) {
                "$F.label"([uikey    : 'loginFailedLabel', styleName: ValoTheme.LABEL_FAILURE,
                            visible  : false, sizeUndefined: null,
                            alignment: Alignment.BOTTOM_CENTER])
                "$F.label"([uikey    : 'loggedOutLabel', styleName: ValoTheme.LABEL_SUCCESS,
                            visible  : false, sizeUndefined: null,
                            alignment: Alignment.BOTTOM_CENTER])
                "$C.formlayout"([uikey    : 'form', sizeUndefined: null,
                                 alignment: Alignment.TOP_CENTER]) {
                    "$F.text"('Username', [uikey: 'userName'])
                    "$F.password"('Password', [uikey: 'password'])
                    "$F.button"('Login', [uikey        : 'loginButton',
                                          styleName    : ValoTheme.BUTTON_PRIMARY,
                                          clickShortcut: ShortcutAction.KeyCode.ENTER,
                                          clickListener: { login() }])
                }
            }
            "$C.vlayout"([uikey    : 'logoutLayout', sizeUndefined: null,
                          alignment: Alignment.MIDDLE_CENTER, visible: false]) {
                "$F.button"('Logout', [uikey        : 'logoutButton',
                                      styleName    : ValoTheme.BUTTON_PRIMARY,
//                                      clickShortcut: ShortcutAction.KeyCode.ENTER,
                                      clickListener: { logout() }])

            }
        }
        init()
        c
    }
    private init(){
        uiComponents = vaadin.uiComponents
        loginFailLabel = uiComponents['login.loginFaildedLabel']
        loggedOutLabel = uiComponents['login.loggedOutLabel']
        loginLayout = uiComponents['login.loginLayout']
        logoutLayout = uiComponents['login.logoutLayout']

        form = uiComponents['login.form']
        userName = uiComponents['login.userName']
        password = uiComponents['login.password']
        login = uiComponents['login.loginButton']
    }

    private void login() {
        def uName = userName.value
        def password = password.value
        loggedIn = userService.login(uName, password)
        if (loggedIn.name == uName) {
            Notification.show('Login erfolgreich', "user ${loggedIn.name}",
                    Notification.Type.HUMANIZED_MESSAGE)
            toptab = uiComponents['toptab']
            browseRoot = uiComponents['browseTab']
            captureRoot = uiComponents['captureTab']
//            forumRoot = uiComponents['forumTab']
            browseRoot.visible = true
            captureRoot.visible = true
//            forumRoot.visible = true

            loginLayout.visible = false
            logoutLayout.visible = true
            toptab.selectedTab = browseRoot
        } else {
            Notification.show('Login Fehler', "user ${loggedIn.name}",
                    Notification.Type.HUMANIZED_MESSAGE)
        }
    }

    private void logout() {
        loggedIn = new UserDto()
        browseRoot.visible = false
        captureRoot.visible = false
//            forumRoot.visible = true

        loginLayout.visible = true
        logoutLayout.visible = false
    }
}
