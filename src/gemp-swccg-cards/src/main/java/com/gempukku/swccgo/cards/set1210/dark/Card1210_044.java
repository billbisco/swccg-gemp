package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Subtype: Utinni
 * Title: Luke? Luuuuke!
 */
public class Card1210_044 extends AbstractUtinniEffect {
    public Card1210_044() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Luke? Luuuuke!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'All right. I'll be right there, Aunt Beru.'");
        setGameText("Deploy on your non-droid character present at a battleground site; character may not be carried from here or move. Target a Jedi at a site. You lose no more than 1 Force to Force drains at target's location. This Utinni Effect canceled when reached by target.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.droid), Filters.at(Filters.battleground_site));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.character, Filters.not(Filters.droid));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.Jedi, Filters.at(Filters.site));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.Jedi;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter jediTarget = Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self, Filters.hasAttached(self)));
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.sameLocationAs(self, jediTarget), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        if (TriggerConditions.isTableChanged(game, effectResult)
                && target != null
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))
                && GameConditions.canBeCanceled(game, self)) {
            GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}
