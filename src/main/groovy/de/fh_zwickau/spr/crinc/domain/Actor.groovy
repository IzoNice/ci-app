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
import de.geobe.util.association.ToOne

import javax.persistence.*

@Entity
class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id

    public getId() { id }

    @ManyToOne
    @JoinColumn(name = 'type_id')
    private ActorType type
    @Transient
    private ToOne<Actor, ActorType> toType = new ToOne<>(
            {this.@type} as IToAny.IGet,
            {o -> this.@type = o},
            this,
            null
    )
    public IToAny<ActorType> getType() {toType}

    @ManyToOne
    @JoinColumn(name = 'origin_id')
    private Origin origin
    @Transient
    private ToOne<Actor, Origin> toOrigin = new ToOne<>(
            {this.@origin} as IToAny.IGet,
            {o -> this.@origin = o},
            this,
            null
    )
    public IToAny<Origin> getOrigin() {toOrigin}

}
