package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AstromechCapacityModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Mission
 * Title: Imperial Aces
 */
public class Card1271_026 extends AbstractMission {
    public Card1271_026() {
        super(Side.DARK, 2, "Imperial Aces", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V, false);
        setGameText("Deploy on table. Tallon Roll is canceled. At battleground systems where you have an Imperial of ability = 2 piloting a starfighter, and you have no Force drain bonus (except from the system card itself), your Force drains are +1. Your starfighters with a pilot character aboard are immune to attrition < 4. Once per turn, may deploy TIE Fighter Construction Facility from Reserve Deck; reshuffle. Your starfighters without astromech capacity gain 'May add 1 astromech.'");
        addIcons(Icon.SETUP, Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter aceSystems = Filters.and(Filters.battleground_system,
                Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.starfighter,
                        Filters.hasPiloting(self, Filters.and(Filters.Imperial, Filters.abilityEqualTo(2))))),
                noForceDrainBonusExceptSystemCard(self, playerId));
        Filter starfightersWithPilot = Filters.and(Filters.your(self), Filters.starfighter, Filters.hasPiloting(self, Filters.character));
        Filter yourStarfighters = Filters.and(Filters.your(self), Filters.starfighter, withoutPrintedAstromechCapacity());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, aceSystems, 1, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, starfightersWithPilot, 4));
        modifiers.add(new AstromechCapacityModifier(self, yourStarfighters, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Tallon_Roll)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Tallon_Roll)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Tallon_Roll, Title.Tallon_Roll);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_ACES__DEPLOY_TIE_FIGHTER_CONSTRUCTION_FACILITY;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy TIE Fighter Construction Facility from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title("TIE Fighter Construction Facility"), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    static Filter withoutPrintedAstromechCapacity() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
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
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
                for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.FORCE_DRAIN_AMOUNT, location)) {
                    if (!modifier.isForPlayer(playerId)) {
                        continue;
                    }
                    if (modifier.getForceDrainModifier(playerId, gameState, modifiersQuerying, location) <= 0) {
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
