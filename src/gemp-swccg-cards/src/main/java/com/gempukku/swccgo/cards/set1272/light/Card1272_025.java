package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitIncreaseLimitModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MaximumDefenseValueModifiedToModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: I Hope She's All Right (V)
 */
public class Card1272_025 extends AbstractNormalEffect {
    public Card1272_025() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.I_Hope_Shes_All_Right, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I'm afraid that Artoo's sensors can find no trace of Princess Leia.'");
        setGameText("Deploy on table. Your scouts are defense value and forfeit +2 (neither may be increased above 6). Your Effects that deploy on Back Door may deploy there for free. Once per turn, may use 2 Force to take an [Endor] battleground site, an [Endor] weapon, or a card with 'Ewok' in lore into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    static Filter cardToTake() {
        return Filters.or(Filters.and(Icon.ENDOR, Filters.battleground_site), Filters.and(Icon.ENDOR, Filters.weapon), Filters.loreContains("Ewok"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourScouts = Filters.and(Filters.your(self), Filters.scout);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, yourScouts, 2));
        modifiers.add(new MaximumDefenseValueModifiedToModifier(self, yourScouts, null, 6));
        modifiers.add(new ForfeitModifier(self, yourScouts, 2));
        // forfeit may not be increased above 6: limit increases above printed forfeit to (6 - printed)
        modifiers.add(new ForfeitIncreaseLimitModifier(self, yourScouts, null, new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                Float printed = cardAffected.getBlueprint().getForfeit();
                return Math.max(0, 6 - (printed != null ? printed : 0));
            }
        }, false));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.and(Filters.your(self), Filters.Effect_of_any_Kind), Filters.Back_Door));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__I_HOPE_SHES_ALL_RIGHT_V__TAKE_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take an [Endor] battleground site, an [Endor] weapon, or a card with 'Ewok' in lore into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, cardToTake(), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
