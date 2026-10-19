package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Imperial
 * Title: Elite Squadron Stormtrooper
 */
public class Card1250_006 extends AbstractImperial {
    public Card1250_006() {
        super(Side.DARK, 3, 3, 3, 2, 3, Title.Elite_Squadron_Stormtrooper, Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("Commander Igar selected the Empire's most dedicated stormtroopers to guard Endor for the Emperor. All they know is killing and white uniforms.");
        setGameText("Clone. Heroic. Deploys -1 if a Dark Jedi at Senate. Power +2 if with Order 66. Jedi here are Heroic. Permanent weapon is blaster rifle (may target a character or vehicle for free; draw destiny; target hit if destiny > defense value).");
        addIcons(Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.EPISODE_I, Icon.ENDOR);
        addKeywords(Keyword.STORMTROOPER, Keyword.GUARD, Keyword.CLONE_TROOPER, Keyword.HEROIC);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.and(Filters.Dark_Jedi, Filters.at(Filters.Galactic_Senate))), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.title("Order 66")), 2));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.Jedi, Filters.here(self)), Keyword.HEROIC));
        return modifiers;
    }

    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon permanentWeapon = new AbstractPermanentWeapon("blaster rifle") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
        permanentWeapon.addKeyword(Keyword.BLASTER_RIFLE);
        return permanentWeapon;
    }
}
