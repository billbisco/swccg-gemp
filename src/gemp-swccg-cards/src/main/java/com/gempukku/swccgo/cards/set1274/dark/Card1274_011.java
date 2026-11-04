package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.effects.LegacyLightsaberHitRules;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Weapon
 * Subtype: Character
 * Title: Cyborg Commander's Lightsabers (renamed Grievous' Lightsabers by later errata)
 */
public class Card1274_011 extends AbstractCharacterWeapon {
    public Card1274_011() {
        super(Side.DARK, 1, "Cyborg Commander's Lightsabers", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setGameText("Deploy on Cyborg Commander. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and may not satisfy attrition (or be removed from Lost Pile this turn), if total destiny > defense value.");
        addIcons(Icon.EPISODE_I);
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(CYBORG_COMMANDER);
    }

    // Cyborg Commander, Hunter Of Jedi (Virtual Block 7.1) is the Grievous persona; match the persona or the printed title
    private static final Filter CYBORG_COMMANDER = Filters.or(Filters.Grievous, Filters.titleContains("Cyborg Commander"));

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), CYBORG_COMMANDER);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return CYBORG_COMMANDER;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self))
                && GameConditions.canUseWeapon(game, self.getAttachedTo(), self)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return LegacyLightsaberHitRules.mayNotBeRemovedFromLostPileThisTurnWhenHit(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            // Target hit, and may not be used to satisfy attrition, if total destiny > defense value
            FireWeaponAction action = actionBuilder.builderDarkAndLightJediLightSaberV();
            return Collections.singletonList(action);
        }
        return null;
    }
}
