package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.set1230.dark.Card1230_007;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Alien
 * Title: Hermi Odle (V)
 * Original version of 1230_7: the Virtual Block 3 release sheet text (Dec 2009). Aliens here "may not ... be targeted by
 * Disarmed" (only the card Disarmed is stopped; other cards may still disarm them), where the current text (PC website card list by Jul 2012,
 * then AR 2.0 Appendix A by Aug 2012) says they may not "be Disarmed" at all. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_007 extends Card1230_007 {
    public Card2230_007() {
        super();
        setGameText("Aliens at same and related sites may not have their weapons stolen or be targeted by Disarmed, and, while armed with a non-lightsaber, non-[Permanent Weapon] weapon: their weapon destiny draws are +1 and opponent's [Permanent Weapon] characters are power -1 there.");
        setLegacyVersion(new LegacyVersion("1230_7", "",
                "Virtual Block 3 release sheet (2009-12-07), card #07E; superseded by the PC forum errata (2011-03-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2011, 3, 21)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter aliensHere = Filters.and(Filters.alien, Filters.atSameOrRelatedLocation(self));
        Filter nonLightsaberNonPermanentWeapon = Filters.and(Filters.weapon, Filters.not(Filters.lightsaber), Filters.not(Icon.PERMANENT_WEAPON));
        Filter theirWeapons = Filters.and(Filters.weapon, Filters.attachedTo(aliensHere));
        Condition armedWithSuchWeapon = new OnTableCondition(self, Filters.and(aliensHere, Filters.armedWith(nonLightsaberNonPermanentWeapon)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeStolenModifier(self, theirWeapons));
        modifiers.add(new MayNotBeTargetedByModifier(self, aliensHere, Filters.title(Title.Disarmed)));
        modifiers.add(new EachWeaponDestinyModifier(self, nonLightsaberNonPermanentWeapon, aliensHere, 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self), Filters.character, Icon.PERMANENT_WEAPON, Filters.atSameOrRelatedLocation(self)), armedWithSuchWeapon, -1));
        return modifiers;
    }
}
