package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.Collections;
import java.util.List;

/** Legacy Virtual Block 6.1: Darth Maul With Lightsaber. */
public class Card1261_006 extends AbstractSith {
    public Card1261_006() {
        super(Side.DARK, 1, 7, 7, 6, 7, "Darth Maul With Lightsaber", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Yes, my Master.'");
        setGameText("Permanent weapon is •Maul's Lightsaber (may target a character or creature for free; draw two destiny; target hit, and its forfeit = 0, if total destiny > defense value). Adds one battle destiny if with Qui-Gon. Immune to Eject! Eject!.");
        addPersona(Persona.MAUL);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
    }

    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon weapon = new AbstractPermanentWeapon(Persona.MAULS_DOUBLE_BLADED_LIGHTSABER, Title.Mauls_Lightsaber) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self,
                    boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring,
                    Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder builder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard,
                                self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter,
                                defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, Filters.creature, targetedAsCharacter), TargetingReason.TO_BE_HIT)
                        .finishBuildPrep();
                return builder == null ? null : Collections.singletonList(builder.buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE, true, 0));
            }
        };
        weapon.addKeyword(Keyword.LIGHTSABER);
        return weapon;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.QuiGon), 1),
                new ImmuneToTitleModifier(self, Title.Eject_Eject));
    }
}
