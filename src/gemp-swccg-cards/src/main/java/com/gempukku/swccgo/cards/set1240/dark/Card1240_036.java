package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.InfluenceModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: There Is No Try & Oppressive Enforcement
 */
public class Card1240_036 extends AbstractNormalEffect {
    public Card1240_036() {
        super(Side.DARK, 1, PlayCardZoneOption.ATTACHED, "There Is No Try & Oppressive Enforcement", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        addComboCardTitles(Title.There_Is_No_Try, Title.Oppressive_Enforcement);
        setGameText("Deploy on Conference Room. Revolution is canceled. You may not deploy aliens (except bounty hunters and Mara Jade) or [Episode I] cards. Emperor and your admirals, generals, and moffs are Imperial Council Members and influence +1. While present here, each Imperial Council Member adds one [Dark Side Force]. Once per turn, may use 1 Force to deploy one Laser Gate or a Political Effect from Reserve Deck; reshuffle. Your cards may deploy to table from Political Effects. May place your canceled Political Effect in Used Pile. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_Conference_Room;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourLeaders = Filters.and(Filters.your(self), Filters.or(Filters.Emperor, Filters.admiral, Filters.general, Filters.moff));
        Filter bannedAliens = Filters.and(Filters.alien, Filters.not(Filters.or(Filters.bounty_hunter, Filters.Mara_Jade)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.Revolution));
        modifiers.add(new MayNotDeployModifier(self, bannedAliens, playerId));
        modifiers.add(new MayNotDeployModifier(self, Icon.EPISODE_I, playerId));
        modifiers.add(new KeywordModifier(self, yourLeaders, Keyword.IMPERIAL_COUNCIL_MEMBER));
        modifiers.add(new InfluenceModifier(self, yourLeaders, 1));
        modifiers.add(new IconModifier(self, Filters.hasAttached(self), new AttachedCondition(self, Filters.Death_Star_Conference_Room), Icon.DARK_FORCE, councilMembersHere(self)));
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.and(Filters.your(self), Filters.Political_Effect)));
        return modifiers;
    }

    private Evaluator councilMembersHere(final PhysicalCard self) {
        return new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return Filters.countActive(gameState.getGame(), self, Filters.and(Keyword.IMPERIAL_COUNCIL_MEMBER, Filters.present(self)));
            }

            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return evaluateExpression(gameState, modifiersQuerying, cardAffected);
            }
        };
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployFromReserveId = GameTextActionId.THERE_IS_NO_TRY__DEPLOY_LASER_GATE_OR_POLITICAL_EFFECT;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, deployFromReserveId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployFromReserveId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployFromReserveId);
            action.setText("Deploy Laser Gate or Political Effect");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Laser_Gate, Filters.Political_Effect), true));
            actions.add(action);
        }

        GameTextActionId deployStackedId = GameTextActionId.OTHER_CARD_ACTION_2;
        Filter yourPoliticalEffectWithStacked = Filters.and(Filters.your(self), Filters.Political_Effect, Filters.hasStacked(Filters.any));
        if (GameConditions.canSpot(game, self, yourPoliticalEffectWithStacked)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployStackedId);
            action.setText("Deploy a card stacked on a Political Effect");
            action.appendEffect(
                    new DeployStackedCardEffect(action, Filters.findFirstActive(game, self, yourPoliticalEffectWithStacked), Filters.any, false));
            actions.add(action);
        }
        return actions;
    }
}
