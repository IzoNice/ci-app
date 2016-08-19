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

package de.fh_zwickau.spr.crinc.domain

import de.geobe.util.association.IToAny
import de.geobe.util.association.ToMany

import javax.persistence.*

@Entity
@Table(name='t_user')
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    public getId() { id }

    String name
    String password

    @OneToMany(mappedBy = 'author')
    private List<CriticalIncident> criticalIncidents = new LinkedList<>()
    @Transient
    private ToMany<User, CriticalIncident> toCriticalIncidents = new ToMany<>(
            {this.@criticalIncidents} as IToAny.IGet,
            this,
//            null
            {ci -> ci.author}
    )
    public IToAny<CriticalIncident> getCriticalIncidents() {toCriticalIncidents}

    public static enum Role {
        expert,
        writer,
        reader,
        admin
    }

    public static enum Status {
        on,
        off
    }
}
