package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Weapon
 * Subtype: Character
 * Title: Guardian's Lightsaber
 */
public class Card1283_029 extends AbstractCharacterWeapon {
    public Card1283_029() {
        super(Side.LIGHT, 1, "Guardian's Lightsaber", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLore("A Jedi learns not only to wield a lightsaber, but how to channel the Force to increase skill and control the lightsaber's damage.");
        setGameText("Deploy on Aayla or [Virtual Block 7] Mace. Once during your control phase, if present at a battleground you control, opponent loses 1 Force. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition, if total destiny > defense value.");
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(carriers());
    }

    private static Filter carriers() {
        return Filters.or(Filters.title("Aayla Secura"), Filters.and(Filters.Mace, Icon.LEGACY_BLOCK_7));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), carriers());
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return carriers();
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GUARDIANS_LIGHTSABER__OPPONENT_LOSES_FORCE;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isPresentAt(game, self, Filters.and(Filters.battleground, Filters.controls(playerId)))) {
            String opponent = game.getOpponent(playerId);
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.builderDarkAndLightJediLightSaberV();
            return Collections.singletonList(action);
        }
        return null;
    }
}
