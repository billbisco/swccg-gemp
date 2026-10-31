package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelEffectsOfRevolutionModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Objective
 * Title: Imperial Occupation / Imperial Control
 */
public class Card1104_040 extends AbstractObjective {
    public Card1104_040() {
        super(Side.DARK, 0, Title.Imperial_Occupation, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Hoth system, Ice Plains, and Main Power Generators (Light Side). May deploy Imperial Decree. For remainder of game, AT-ATs deploy -1. May not deploy Colo Claw Fish. Revolution canceled. While this side up, AT-ATs may move for free. Rebels deploy -1 to Hoth sites. Opponent loses no Force to You May Start Your Landing. During control phase, may take one AT-AT Cannon, Electro-Rangefinder, Death Squadron, a [Virtual] AT-AT, or a [Virtual] Epic Event from Reserve Deck; reshuffle. Flip if Main Power Generators 'blown away.'");
        addIcons(Icon.HOTH);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Hoth_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Hoth system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Ice_Plains, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Ice Plains to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Main_Power_Generators, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Main Power Generators to deploy";
                    }
                });
        action.appendOptionalEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Imperial_Decree), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Imperial Decree to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.AT_AT, -1));
        modifiers.add(new MayNotPlayModifier(self, Filters.title(Title.Colo_Claw_Fish), playerId));
        modifiers.add(new CancelEffectsOfRevolutionModifier(self, null));
        modifiers.add(new SuspendsCardModifier(self, Filters.Revolution));
        modifiers.add(new MovesForFreeModifier(self, Filters.AT_AT));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Rebel, -1, Filters.Hoth_site));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.title(Title.You_May_Start_Your_Landing), opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter virtualATATOrEpicEvent = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint() != null
                        && physicalCard.getBlueprint().hasVirtualSuffix()
                        && Filters.or(Filters.AT_AT, Filters.Epic_Event).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
        Filter takeFilter = Filters.or(
                Filters.title(Title.AT_AT_Cannon),
                Filters.title("Electro-Rangefinder"),
                Filters.title(Title.Death_Squadron),
                virtualATATOrEpicEvent);

        GameTextActionId takeActionId = GameTextActionId.OVS__IMPERIAL_OCCUPATION__TAKE_CARD;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, takeActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, takeActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take an AT-AT Cannon, Electro-Rangefinder, Death Squadron, a [Virtual] AT-AT, or a [Virtual] Epic Event into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, takeFilter, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.title(Title.Main_Power_Generators, true))
                && GameConditions.canBeFlipped(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
