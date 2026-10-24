package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Lars' Moisture Farm
 */
public class Card1210_174 extends AbstractSite {
    public Card1210_174() {
        super(Side.LIGHT, Title.Lars_Moisture_Farm, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Once per game, may deploy a non-[Reflections 2] Lars here from Reserve Deck; reshuffle. While a Lars present here with a hydroponics station or vaporator, your Force generation here is +1 (+2 if with both).");
        setLocationDarkSideGameText("While No One To Stop Us This Time on table, Imperials are deploy -1 here.");
        setGameText("Light: Once per game, may deploy a non-[Reflections 2] Lars here from Reserve Deck; reshuffle. While a Lars present here with a hydroponics station or vaporator, your Force generation here is +1 (+2 if with both). Dark: While No One To Stop Us This Time on table, Imperials are deploy -1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeyword(Keyword.FARM);
    }

    private Filter larsFilter() {
        return Filters.and(Filters.character, Filters.or(Filters.Owen, Filters.Beru, Filters.titleContains("Lars")));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter hydroponics = Filters.title("Lars' Hydroponics Station");
        Filter vaporator = Filters.title("Lars' Vaporator");
        Filter hydroOrVaporator = Filters.or(hydroponics, vaporator);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self,
                new AndCondition(new PresentCondition(self, larsFilter()), new HereCondition(self, hydroOrVaporator)),
                new ConditionEvaluator(1, 2, new AndCondition(new HereCondition(self, hydroponics), new HereCondition(self, vaporator))),
                playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial,
                new OnTableCondition(self, Filters.No_One_To_Stop_Us_This_Time), -1, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LARS_MOISTURE_FARM_V__DEPLOY_LARS;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Lars from Reserve Deck");
            action.setActionMsg("Deploy a non-[Reflections II] Lars here from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(larsFilter(), Filters.not(Filters.icon(Icon.REFLECTIONS_II))), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
