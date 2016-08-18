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

package de.fh_zwickau.spr.crinc.service

import de.fh_zwickau.spr.crinc.domain.*
import de.fh_zwickau.spr.crinc.repository.*
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.time.LocalDateTime

@Service
@Slf4j
class StartupService implements IStartupService {
    private boolean isInitialized = false

    @Autowired
    private CriticalIncidentRepository criticalIncidentRepository
    @Autowired
    private MediumRepository mediumRepository
    @Autowired
    private UserRepository userRepository
    @Autowired
    private CountryRepository countryRepository
    @Autowired
    private OriginRepository originRepository
    @Autowired
    private ActorTypeRepository actorTypeRepository
    @Autowired
    private FieldOfContactRepository fieldOfContactRepository
    @Autowired
    private TypeOfInteractionRepository typeOfInteractionRepository
    @Autowired
    private HotspotRepository hotspotRepository
    @Autowired
    private LanguageRepository languageRepository

    @Override
    @Transactional
    void initApplicationData() {
        criticalIncidentRepository.deleteAll()
        originRepository.deleteAll()
        mediumRepository.deleteAll()
        actorTypeRepository.deleteAll()
        fieldOfContactRepository.deleteAll()
        typeOfInteractionRepository.deleteAll()
        hotspotRepository.deleteAll()
        if (!isInitialized) {
            log.info("initializing data at ${LocalDateTime.now()}")
            ['Deutschland', 'Spanien', 'Frankreich', 'Russland', 'Algerien', 'Jordanien',
             'Slowenien', 'Albanien', 'Griechenland', 'Türkei', 'Rumänien', 'Georgien',
             'Tschechien', 'Japan', 'Philippinen', 'Kanada', 'Bulgarien', '(Süd)korea',
             'Ungarn', 'Irak', 'Großbritannien', 'USA', 'Kongo', 'China', 'Polen', 'Indien',
             'Brasilien', 'Italien', 'Nigeria', 'Costa Rica', 'Schweden', 'Iran', 'Thailand',
             'Irland', 'Luxenburg', 'Kuba', 'Uruguay', 'Saudi-Arabien', 'Slowakei', 'Ägypten',
             'Schweiz', 'Niederlande', 'Tunesien', 'Kasachstan', 'Argentinien', 'Belgien',
            ].each {
                def country = new Country([name: it, label: it])
                countryRepository.saveAndFlush(country)
            }
            ['Araber', 'Afrika', 'Asien', 'Sowjetunion', 'Sibirien', 'Kurde',
            ].each {
                def origin = new Origin([label: it])
                originRepository.saveAndFlush(origin)
            }
            ['Deutsch', 'Englisch', 'Französisch', 'Spanisch'
            ].each {
                def lang = new Language([label: it])
                languageRepository.saveAndFlush(lang)
            }
            ['Arbeitsplatz', 'Asyl/Flucht', 'Ausbildungsstätte', 'Behörde/Amt', 'Disco/Kneipe',
             'Ehe', 'Familie', 'Freizeit/Sport', 'Freundeskreis/Bekannte', 'Geschäfte/Läden',
             'Intime Beziehung', 'Kindergarten', 'Krankenhaus/Arzt', 'Markt', 'Nachbarschaft',
             'Öffentlicher Raum/Straße/Platz', 'Polizei/Justiz', 'Praktikum', 'Privater ' +
                     'Raum/Wohnung', 'Religiöse Einrichtungen', 'Restaurant/Imbiss', 'Schule',
             'Soziale Einrichtungen', 'Studium und studentisches Leben', 'Tourismus/Urlaub',
             'Verkehrsmittel'
            ].each {
                def foc = new FieldOfContact(label: it)
                fieldOfContactRepository.saveAndFlush(foc)
            }
            ['Kind', 'Jugendliche', 'Jugendlicher', 'Frau', 'Mann', 'alte Frau', 'alter Mann',
             'anonyme Menge'].each {
                def actorType = new ActorType(label: it)
                actorTypeRepository.saveAndFlush(actorType)
            }
            [
                    ['co-präsent', ''],
                    ['medial vermittelt', 'Telefon'],
                    ['medial vermittelt', 'Skype u.ä.'],
                    ['medial vermittelt', 'Videokonferenz'],
                    ['schriftlich', 'Brief'],
                    ['schriftlich', 'Notiz'],
                    ['schriftlich', 'Email'],
                    ['schriftlich', 'Chat'],
                    ['schriftlich', 'Forum'],
                    ['schriftlich', 'sms'],
                    ['schriftlich', 'WhatsApp u.ä.']
            ].each {
                def toi = new TypeOfInteraction(itype: it[0], label: it[1])
                typeOfInteractionRepository.saveAndFlush(toi)
            }

            ['Anreden und Namen', 'Beenden eines Kontakts', 'Begrüßen', 'Einladungen', 'Flirten ',
             'Geschenke', 'Gesprächsverlauf und Redeübernahme', 'Humor ',
             'Ja und Nein sagen/etwas annehmen und ablehnen',
             'Jemanden überzeugen', 'Knüpfen eines Kontakts',
             'Kritik äußern', 'Nach dem Weg fragen', 'Persönliche Fragen', 'Schweigen',
             'Sich entschuldigen', 'Sich verabreden', 'Sich vorstellen', 'Themen und Topiks',
             'Welche Sprache?', 'Zeitverständnis', 'Zuhörgewohnheiten'
            ].each {
                def hotspot = new Hotspot(label: it)
                hotspotRepository.saveAndFlush(hotspot)
            }

            def usr0 = new User([name: 'Doris', password: 'Ding'])
            userRepository.saveAndFlush(usr0)
            def usr1 = new User([name: 'FFK', password: 'Ding'])
            userRepository.saveAndFlush(usr1)
            def usr2 = new User([name: 'Christoph', password: 'Ding'])
            userRepository.saveAndFlush(usr2)
            def usr3 = new User([name: 'a', password: 'a'])
            userRepository.saveAndFlush(usr3)

            def ciAuthor = userRepository.findAll()[0]
            def langDE = languageRepository.findByLabelLike('Deut%')[0]
            def cntryDE = countryRepository.findByNameLike('Deut%')[0]
            def cntryBG = countryRepository.findByNameLike('Bulg%')[0]
            def cntryHU = countryRepository.findByNameLike('Unga%')[0]


            def ci = new CriticalIncident(
                    [shortName: 'f24', header: 'Essen von Gastgeber ablehnen', paraverbal: true,
                     ciOrigin : CriticalIncident.CiOrigin.OwnExperience])
            def citxt = new Text([
                    story: """
Die zweite Fallgeschichte ist mit meinem Freund passiert,
 als er bei mir in Bulgarien war. Wir waren zu Besuch bei einer bulgarischen Familie.
 Die Gastgeberin war typisch bulgarisch sehr gastfreundlich, indem sie jedem Gast eine
 riesig große Portion ausgeteilt hat.(Mit Salat, Vorspeise und Hauptgericht.)
 Mein Freund hatte aber vorher schon gegessen, weil wir nicht wussten, dass wir Abendbrot krigen.
 Er hatte keinen Hunger, hat sich aber verpflichtet gefüllt alles aufzuessen.
 Er hat gedacht er beleidigt die Gastgeberin, falls etwas übrig bleibt.
 Ich habe keine Ahnung wie er in Deutschland handeln würde, ich vermute aber,
 dass er keine Zögerungen hätte abzulehnen. Ich habe ihm danach erklärt,
 dass es in Bulgarien nicht unhöfflich wäre, wenn man einfach erklärt,
 dass man nichts möchte."""])
            citxt.language.add(langDE)
            ci.countryOfHappening.add(cntryBG)
            ci.mediums.add(citxt)
            ci.author.add(ciAuthor)
            def actor1 = new Actor()
            def actor2 = new Actor()
            def mann = actorTypeRepository.findByLabelLike('Ma%')[0]
            def frau = actorTypeRepository.findByLabelLike('Fr%')[0]
            actor1.type.add(mann)
            actor2.type.add(frau)
            actor1.origin.add(cntryDE)
            actor2.origin.add(cntryBG)
            ci.actors.add(actor1)
            ci.actors.add(actor2)
            def foc = fieldOfContactRepository.findByLabelLike('Famil%')[0]
            ci.fieldsOfContact.add(foc)
            def hots = hotspotRepository.findByLabelLike('Einla%')[0]
            ci.hotspots.add(hots)
            criticalIncidentRepository.saveAndFlush(ci)

            def ci2 = new CriticalIncident(
                    [shortName: 'f25', header: 'Begruessung', proxematic: true,
                     ciOrigin : CriticalIncident.CiOrigin.Captured])
            def ci2txt = new Text([story: """
Ich bin Ungarin. Bei uns begrüßt man Menschen, die man kaum kennt nur mit Worten ohne Umarmung
oder Küsschen.
Neulich traf ich ein Mädchen an der Uni Augsburg, das mich bei der Begrüßung umarmte.
Ich habe mit der Situation nicht gerechnet, so stand ich ohne mich zu bewegen da.
Es lief alles sehr überraschend und schnell ab; ich konnte nicht einschätzen, was genau passiert
und was
 von mir erwartet wird.
Das Mädchen reagierte auch verwirrt auf mein Verhalten; ich merkte gleich, dass ich was sagen
soll, damit
 es nicht denkt, ich sei unhöflich oder ich würde es nicht mögen.
Wir konnten die Situation klären."""])
            ci2.mediums.add(ci2txt)
            ci2txt.language.add(langDE)
            ci2.countryOfHappening.add(cntryDE)
            ci2.author.add(ciAuthor)
            actor1 = new Actor()
            actor2 = new Actor()
            actor1.type.add(frau)
            actor2.type.add(frau)
            actor1.origin.add(cntryHU)
            actor2.origin.add(cntryDE)
            ci2.actors.add(actor1)
            ci2.actors.add(actor2)
            foc = fieldOfContactRepository.findByLabelLike('Studi%')[0]
            ci2.fieldsOfContact.add(foc)
            hots = hotspotRepository.findByLabelLike('Begr%')[0]
            ci2.hotspots.add(hots)
            criticalIncidentRepository.saveAndFlush(ci2)

            isInitialized = true
        }
    }
}
