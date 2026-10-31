package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.UndercoverCondition;
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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Character
 * Subtype: Rebel
 * Title: Orrimaarko
 */
public class Card1112_017 extends AbstractRebel {
    public Card1112_017() {
        super(Side.LIGHT, 1, 5, 4, 4, 4, Title.Orrimaarko, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Dresselian Scout and resistance leader. Worked tirelessly to combat the subjugation of his homeworld before Bothans brought him into contact with the Alliance.");
        setGameText("Once per game, may take a rifle and/or an Interrupt with 'scout' in game text into hand from Reserve Deck; reshuffle. Unless Undercover, while alone at an exterior site, opponent's weapon destiny draws here are -2. Immune to attrition < 4.");
        addIcons(Icon.WARRIOR, Icon.ENDOR);
        setSpecies(Species.DRESSELIAN);
        addKeywords(Keyword.SCOUT, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.opponents(self), Filters.weapon, Filters.here(self)),
                new AndCondition(new NotCondition(new UndercoverCondition(self)), new AloneCondition(self), new AtCondition(self, Filters.exterior_site)), -2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ORRIMAARKO_V__TAKE_RIFLE_OR_INTERRUPT;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take rifle and/or Interrupt into hand");
            action.setActionMsg("Take a rifle and/or an Interrupt with 'scout' in game text into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 1, 2,
                            Filters.or(Filters.rifle, Filters.and(Filters.Interrupt, Filters.gameTextContains("scout"))), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
