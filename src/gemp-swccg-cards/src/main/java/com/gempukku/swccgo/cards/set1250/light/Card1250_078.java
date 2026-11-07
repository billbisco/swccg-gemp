package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5 We Have A Plan, front of printing 78. */
public class Card1250_078 extends AbstractObjective {
    private static final Filter PRISON = Filters.title("Blockade Flagship: Prison");

    public Card1250_078() {
        super(Side.LIGHT, 0, Title.We_Have_A_Plan, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Coruscant system (may not be converted) and Blockade Flagship: Prison with Palpatine imprisoned there. May deploy Security Control. For remainder of game, your Force generation is +1 at Coruscant battlegrounds. Unless you have 13 or more cards in hand, clones are immune to Monnok. While this side up, at Blockade Flagship sites, imprisoned captives may not be transferred. Once per turn, may deploy a battleground site to Coruscant from Reserve Deck; reshuffle. Flip this card if Palpatine on Coruscant.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_system, true, false) {
            @Override
            public String getChoiceText() { return "Choose Coruscant system to deploy"; }
        });
        action.appendRequiredEffect(new DeployCardFromReserveDeckEffect(action, PRISON, true, false) {
            @Override
            public String getChoiceText() { return "Choose Blockade Flagship: Prison to deploy"; }
        });
        action.appendRequiredEffect(new DeployCardToTargetFromReserveDeckEffect(action, Filters.Palpatine, PRISON,
                true, null, DeployAsCaptiveOption.deployAsImprisonedCaptive(), false) {
            @Override
            public String getChoiceText() { return "Choose Palpatine to imprison"; }
        });
        action.appendOptionalEffect(new DeployCardsFromReserveDeckEffect(action, Filters.title("Security Control"),
                0, 1, true, false) {
            @Override
            public String getChoiceText(int numCardsToChoose) { return "Choose Security Control to deploy"; }
        });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId,
            SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        var action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new MayNotBeConvertedModifier(self, Filters.Coruscant_system), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new ForceGenerationModifier(self, Filters.and(Filters.Coruscant_location, Filters.battleground),
                        1, playerId), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new ImmuneToTitleModifier(self, Filters.clone, new CardsInHandFewerThanCondition(playerId, 13),
                        Title.Monnok), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotBeTransferredModifier(self, Filters.and(Filters.imprisoned,
                Filters.at(Filters.siteOfStarshipOrVehicle(Persona.BLOCKADE_FLAGSHIP, false)))));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        var id = GameTextActionId.LEGACY__WE_HAVE_A_PLAN__DEPLOY_CORUSCANT_SITE;
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) return null;

        var action = new TopLevelGameTextAction(self, gameTextSourceCardId, id);
        action.setText("Deploy a battleground site to Coruscant");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new DeployCardToSystemFromReserveDeckEffect(action, Filters.battleground_site,
                Title.Coruscant, true));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isTableChanged(game, result) || !GameConditions.canBeFlipped(game, self)
                || !Filters.canSpot(game, self, Filters.and(Filters.your(self), Filters.Palpatine,
                        Filters.on(Title.Coruscant)))) return null;

        var action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Flip");
        action.appendEffect(new FlipCardEffect(action, self));
        return List.of(action);
    }
}
