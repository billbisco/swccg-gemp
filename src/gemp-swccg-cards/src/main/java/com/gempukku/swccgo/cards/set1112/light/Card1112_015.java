package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Character
 * Subtype: Republic
 * Title: Ki-Adi-Mundi (V)
 */
public class Card1112_015 extends AbstractRepublic {
    public Card1112_015() {
        super(Side.LIGHT, 2, 6, 5, 6, 5, "Ki-Adi-Mundi", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Cerean Jedi trained by Yoda since the age of four. Only Jedi Council member who is a Jedi Knight. Freed his homeworld from a group of rogues without any bloodshed.");
        setGameText("While Maul present, Ki-Adi-Mundi is Heroic. Obi-Wan's Cape, Obi-Wan's Journal, and [Virtual Set 9] Jedi Lightsaber may deploy on Ki-Adi-Mundi. Once per game, may exchange two cards from hand with any one card in Lost Pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
        setSpecies(Species.CEREAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter jediLightsaber = Filters.and(Filters.title(Title.Jedi_Lightsaber), virtualOriginalSet9());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, new WithCondition(self, Filters.and(Filters.Maul, Filters.presentAt(Filters.here(self)))), Keyword.HEROIC));
        modifiers.add(new MayDeployToTargetModifier(self, Filters.or(Filters.title("Obi-Wan's Cape"), Filters.ObiWans_Journal, jediLightsaber), self));
        modifiers.add(new MayUseWeaponModifier(self, jediLightsaber));
        return modifiers;
    }

    private static Filter virtualOriginalSet9() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_9;
            }
        };
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__KI_ADI_MUNDI__EXCHANGE_CARDS_FROM_HAND_WITH_CARD_IN_LOST_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.numCardsInHand(game, playerId) >= 2
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange cards with card in Lost Pile");
            action.setActionMsg("Exchange two cards in hand with a card in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardsInHandWithCardInLostPileEffect(action, playerId, 2, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
