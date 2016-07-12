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

import de.geobe.util.association.IGetOther
import de.geobe.util.association.IToAny
import de.geobe.util.association.ToMany
import de.geobe.util.association.ToOne

import javax.persistence.*

@Entity
class CriticalIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id
    public getId() {id}
    String shortName
    String header
    CiOrigin ciOrigin
    Boolean verbal = false
    Boolean nonVerbal = false
    Boolean paraverbal = false
    Boolean proxematic = false

    @ManyToOne
    @JoinColumn(name = 'author_ci')
    private User author
    @Transient
    private ToOne<CriticalIncident, User> toAuthor = new ToOne<>(
            {this.@author} as IToAny.IGet,
            {o -> this.@author = o},
            this,
            {o -> o.criticalIncidents} as IGetOther
    )
    public IToAny<User> getAuthor() {toAuthor}

    @ManyToOne
    @JoinColumn(name = 'interactiontype_id')
    private TypeOfInteraction typeOfInteraction
    @Transient
    private ToOne<CriticalIncident, TypeOfInteraction> toTypeOfInteraction = new ToOne<>(
            {this.@typeOfInteraction} as IToAny.IGet,
            {o -> this.@typeOfInteraction = o},
            this,
            null
    )
    public IToAny<TypeOfInteraction> getTypeOfInteraction() {toTypeOfInteraction}

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = 'medium_id')
    private List<Medium> mediums = new LinkedList<>()
    @Transient
    private ToMany<CriticalIncident, Medium> toMediums = new ToMany<>(
            { this.@mediums } as IToAny.IGet,
            this,
            null
    )
    public getMediums() { toMediums }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = 'actor_id')
    private List<Actor> actors = new LinkedList<>()
    @Transient
    private ToMany<CriticalIncident, Actor> toActors = new ToMany<>(
            {this.@actors} as IToAny.IGet,
            this,
            null
    )
    public IToAny<Actor> getActors() {toActors}

    @ManyToOne
    @JoinColumn(name = 'country_id')
    private Country countryOfHappening
    @Transient
    private ToOne<CriticalIncident, Country> toCountry = new ToOne<>(
            {this.@countryOfHappening} as IToAny.IGet,
            {o -> this.@countryOfHappening = o},
            this,
            null
    )
    public IToAny<Country> getCountryOfHappening() {toCountry}

    @ManyToMany
    @JoinTable(name = 'join_chriticalincident_hotspot',
            joinColumns = @JoinColumn(name = 'criticalincident_id'),
            inverseJoinColumns = @JoinColumn(name = 'hotspot_id'))
    private List<Hotspot> hotspots = new LinkedList<>()
    @Transient
    private ToMany<CriticalIncident, Hotspot> toHotspots = new ToMany<>(
            {this.@hotspots} as IToAny.IGet,
            this,
            null
    )
    public IToAny<Hotspot> getHotspots() {toHotspots}

    @ManyToMany
    @JoinTable(name = 'join_chriticalincident_contactfield',
            joinColumns = @JoinColumn(name = 'criticalincident_id'),
            inverseJoinColumns = @JoinColumn(name = 'contactfield_id'))
    private List<FieldOfContact> fieldsOfContact = new LinkedList<>()
    @Transient
    private ToMany<CriticalIncident, FieldOfContact> toFieldsOfContact = new ToMany<>(
            {this.@fieldsOfContact} as IToAny.IGet,
            this,
            null
    )
    public IToAny<FieldOfContact> getFieldsOfContact() {toFieldsOfContact}

    public static enum CiOrigin {
        OwnExperience,
        Captured,
        Hearsay
    }

}
