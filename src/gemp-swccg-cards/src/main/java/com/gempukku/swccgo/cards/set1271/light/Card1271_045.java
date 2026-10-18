package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AstromechCapacityModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Mission
 * Title: Rebel Aces
 */
public class Card1271_045 extends AbstractMission {
    public Card1271_045() {
        super(Side.LIGHT, 2, "Rebel Aces", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V, false);
        setGameText("Deploy on table. Your Sandwhirl is canceled. At battleground systems where you have a Rebel of ability = 2 piloting a starfighter, and you have no Force drain bonus (except from the system card itself), your Force drains are +1. Your starfighters with a pilot character aboard are immune to attrition < 4. Once per turn, may deploy Close Air Support from Reserve Deck; reshuffle. Your Rebel starfighters without astromech capacity (except Falcon or Artoo-Detoo In Red 5) gain 'May add 1 astromech.'");
        addIcons(Icon.SETUP, Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter aceSystems = Filters.and(Filters.battleground_system,
                Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.starfighter,
                        Filters.hasPiloting(self, Filters.and(Filters.Rebel, Filters.abilityEqualTo(2))))),
                noForceDrainBonusExceptSystemCard(self, playerId));
        Filter starfightersWithPilot = Filters.and(Filters.your(self), Filters.starfighter, Filters.hasPiloting(self, Filters.character));
        Filter rebelStarfighters = Filters.and(Filters.your(self), Filters.Rebel_starfighter, withoutPrintedAstromechCapacity(),
                Filters.except(Filters.or(Filters.Falcon, Filters.title("Artoo-Detoo In Red 5"))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, aceSystems, 1, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, starfightersWithPilot, 4));
        modifiers.add(new AstromechCapacityModifier(self, rebelStarfighters, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter yourSandwhirl = Filters.and(Filters.your(self), Filters.Sandwhirl);
        if (TriggerConditions.isPlayingCard(game, effect, yourSandwhirl)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter yourSandwhirl = Filters.and(Filters.your(self), Filters.Sandwhirl);
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, yourSandwhirl)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, yourSandwhirl, Title.Sandwhirl);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__REBEL_ACES__DEPLOY_CLOSE_AIR_SUPPORT;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Close Air Support from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title("Close Air Support"), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    static Filter withoutPrintedAstromechCapacity() {
        return new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                try {
                    return physicalCard.getBlueprint().getAstromechCapacity() == 0;
                } catch (UnsupportedOperationException e) {
                    return false;
                }
            }
        };
    }

    static Filter noForceDrainBonusExceptSystemCard(final PhysicalCard self, final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard location) {
                for (com.gempukku.swccgo.logic.modifiers.Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, com.gempukku.swccgo.logic.modifiers.ModifierType.FORCE_DRAIN_AMOUNT, location)) {
                    if (!modifier.isForPlayer(playerId)) {
                        continue;
                    }
                    PhysicalCard source = modifier.getSource(gameState);
                    if (source != null && source != location && source != self) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
