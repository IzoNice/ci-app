/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016.  Georg Beier. All rights reserved.
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

package de.fh_zwickau.spr.crinc.service

import de.fh_zwickau.spr.crinc.domain.User
import de.fh_zwickau.spr.crinc.dto.UserDto
import de.fh_zwickau.spr.crinc.repository.UserRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Service
@Slf4j

class UserService {
    @Autowired
    private UserRepository userRepository

    @Transactional
    public UserDto login(UserDto userDto) {
        User user = userRepository.findByName(userDto.name)
        if (user) {
            if (user.password == userDto.password) {
                // password niemals an die GUI senden
                return new UserDto(id: user.id, name: user.name, password: '')
            }
        } else {
            return new UserDto()
        }
    }

    @Transactional
    public UserDto getUser(Long id) {
        User user = userRepository.getOne(id)
        if (user) {
            // password niemals an die GUI senden
            return new UserDto(id: user.id, name: user.name, password: '')
        } else {
            return new UserDto()
        }
    }

}
