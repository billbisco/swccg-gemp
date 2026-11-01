package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Scum and Villainy
 * Type: Defensive Shield
 * Title: After Her! (V)
 */
public class Card1114_018 extends AbstractDefensiveShield {
    public Card1114_018() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "After Her!", ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setLore("'This one's a decoy!'");
        setGameText("Plays on table. Unless present at a site, [Reflections III] Leia's game text is canceled. Each Amidala stacked on a Political Effect is a senator. Once per game, may take [Virtual Set 3] Astromech Shortage into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.EPISODE_I, Icon.THEED_PALACE);
    }

    @Override
    public Float getDestiny() {
        return 5f;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter reflections3Leia = Filters.and(Filters.Leia, Icon.REFLECTIONS_III);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, reflections3Leia, new UnlessCondition(new PresentAtCondition(reflections3Leia, Filters.site))));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.Amidala, Filters.stackedOn(self, Filters.Political_Effect)), Keyword.SENATOR));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__AFTER_HER_V__TAKE_ASTROMECH_SHORTAGE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take [Virtual Block 1] Astromech Shortage into hand");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.title(Title.Astromech_Shortage), new com.gempukku.swccgo.filters.Filter() {
                        @Override
                        public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                            return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_3;
                        }
                    }), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
