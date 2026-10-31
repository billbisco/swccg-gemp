package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
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
 * Set: Virtual Set 11: Gathering Storm
 * Type: Effect
 * Subtype: Utinni
 * Title: Let's Keep A Little Optimism Here
 */
public class Card1111_008 extends AbstractUtinniEffect {
    public Card1111_008() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Lets_Keep_A_Little_Optimism_Here, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("The heroes of the Rebellion know that where there is life, there is hope.");
        setGameText("Deploy on your non-droid character present at a battleground site; character may not be carried from here or move. Target a Dark Jedi at a site. You lose no more than 1 Force to Force drains at target's location. This Utinni Effect canceled when reached by target.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.DEATH_STAR_II);
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
        return Filters.and(Filters.Dark_Jedi, Filters.at(Filters.site));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.Dark_Jedi;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter darkJediTarget = Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self, Filters.hasAttached(self)));
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.sameLocationAs(self, darkJediTarget), 1, playerId));
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
