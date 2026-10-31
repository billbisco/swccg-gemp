package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Imperial
 * Title: Tarkin
 */
public class Card1103_047 extends AbstractImperial {
    public Card1103_047() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Tarkin", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial Governor of the Seswenna Sector. Conceived and implemented the Death Star project. A leader in the effort to crush the Rebellion.");
        setGameText("Adds one battle destiny if with Vader (or a Rebel at a mobile site). Jar Jar and opponent's spies may not deploy to Death Star. Once per game may take Hyperwave Scan, Put All Sections On Alert, or an ISB agent into hand from Reserve Deck; reshuffle.");
        addPersona(Persona.TARKIN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withVaderOrRebelAtMobileSite = new OrCondition(
                new WithCondition(self, Filters.Vader),
                new AndCondition(new WithCondition(self, Filters.Rebel), new AtCondition(self, Filters.mobile_site)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, withVaderOrRebelAtMobileSite, 1));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.or(Filters.Jar_Jar, Filters.and(Filters.opponents(self), Filters.spy)), Filters.Death_Star_location));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TARKIN_V__TAKE_CARD;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Hyperwave Scan, Put All Sections On Alert, or an ISB agent into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Hyperwave_Scan, Filters.title(Title.Put_All_Sections_On_Alert), Filters.ISB_agent), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
