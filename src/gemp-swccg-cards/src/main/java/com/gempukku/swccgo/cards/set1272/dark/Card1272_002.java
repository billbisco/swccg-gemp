package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Character
 * Subtype: Imperial
 * Title: ISB Central Commander
 */
public class Card1272_002 extends AbstractImperial {
    public Card1272_002() {
        super(Side.DARK, 1, 3, 3, 3, 5, "ISB Central Commander", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.C);
        setInfluence(1);
        setLore("Imperial council member. ISB leader.");
        setGameText("Agenda: intelligence. While with an ISB agent or Imperial Council Member, may not be targeted by weapons. During your deploy phase, may exchange an Imperial in hand with an Imperial of ability < 5 in Lost Pile. Immune to attrition < 3.");
        addIcons(Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.INTELLIGENCE));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new OrCondition(
                new WithCondition(self, Keyword.ISB_AGENT),
                new WithCondition(self, Keyword.IMPERIAL_COUNCIL_MEMBER))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ISB_CENTRAL_COMMANDER__EXCHANGE_IMPERIAL;

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, Filters.Imperial)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)
                && Filters.canSpot(game.getGameState().getLostPile(playerId), game, Filters.and(Filters.Imperial, Filters.abilityLessThan(5)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange an Imperial with Lost Pile");
            action.setActionMsg("Exchange an Imperial in hand with an Imperial of ability < 5 in Lost Pile");
            action.appendEffect(
                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.Imperial, Filters.and(Filters.Imperial, Filters.abilityLessThan(5))));
            return Collections.singletonList(action);
        }
        return null;
    }
}
