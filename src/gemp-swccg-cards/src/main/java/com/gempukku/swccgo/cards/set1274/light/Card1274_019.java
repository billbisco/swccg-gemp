package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Title: Rebel Sacrifice
 */
public class Card1274_019 extends AbstractNormalEffect {
    public Card1274_019() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Rebel Sacrifice", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setLore("'You can't win Darth. If you strike me down I shall become more powerful than you can possibly imagine.' Obi-Wan's sacrifice gave the Rebels time to escape.");
        setGameText("Deploy on table if Obi-Wan 'communing.' 'Tatooine' on Obi-Wan is treated as 'Death Star.' Your Death Star sites gain one [Light Side], and if opponent loses Force to battle damage (or a Force drain) at such sites, stack the first card lost face-down here. Attack Run is canceled. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.ObiWan);
    }

    private static Filter yourDeathStarSites(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.Death_Star_site, Filters.in_play);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.ObiWan, ModifyGameTextType.LEGACY__MASTER_KENOBI__TATOOINE_TREATED_AS_DEATH_STAR));
        modifiers.add(new IconModifier(self, yourDeathStarSites(self), Icon.LIGHT_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Attack_Run)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String opponent = game.getOpponent(self.getOwner());
        Filter suchSites = yourDeathStarSites(self);

        // Stack the first card lost to a Force drain at your Death Star sites
        if (TriggerConditions.justLostForceFromForceDrainAt(game, effectResult, opponent, suchSites, true)) {
            PhysicalCard cardToStack = ((LostForceResult) effectResult).getCardLost();
            if (cardToStack != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack first card lost face-down");
                action.appendEffect(
                        new StackOneCardFromLostPileEffect(action, cardToStack, self, true, true, true));
                actions.add(action);
            }
        }

        // Stack the first card lost to battle damage at your Death Star sites
        GameTextActionId battleDamageId = GameTextActionId.LEGACY__REBEL_SACRIFICE__STACK_BATTLE_DAMAGE;
        if (TriggerConditions.justLostForceFromBattleDamage(game, effectResult, opponent)
                && game.getGameState().getBattleLocation() != null
                && suchSites.accepts(game, game.getGameState().getBattleLocation())
                && GameConditions.isOncePerBattle(game, self, gameTextSourceCardId, battleDamageId)) {
            PhysicalCard cardToStack = ((LostForceResult) effectResult).getCardLost();
            if (cardToStack != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, battleDamageId);
                action.setText("Stack first card lost face-down");
                action.appendUsage(
                        new OncePerBattleEffect(action));
                action.appendEffect(
                        new StackOneCardFromLostPileEffect(action, cardToStack, self, true, true, true));
                actions.add(action);
            }
        }

        // Attack Run is canceled
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Attack_Run)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Attack_Run, Title.Attack_Run);
            actions.add(action);
        }
        return actions;
    }
}
