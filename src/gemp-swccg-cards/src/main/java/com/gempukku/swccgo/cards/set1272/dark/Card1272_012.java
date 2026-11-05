package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Forest Maze
 */
public class Card1272_012 extends AbstractSite {
    public Card1272_012() {
        super(Side.DARK, "Kashyyyk: Forest Maze", Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setLocationDarkSideGameText("During your control phase, if you occupy with at least two Trandoshans, opponent loses 1 Force.");
        setLocationLightSideGameText("Unless you control, Jedi deploy +1 to Kashyyyk and may 'swing' lightsabers only once per battle there.");
        setGameText("Dark: During your control phase, if you occupy with at least two Trandoshans, opponent loses 1 Force. Light: Unless you control, Jedi deploy +1 to Kashyyyk and may 'swing' lightsabers only once per battle there.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__FOREST_MAZE__OPPONENT_LOSES_FORCE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.occupies(game, playerOnDarkSideOfLocation, self)
                && Filters.countActive(game, self, Filters.and(Filters.owner(playerOnDarkSideOfLocation), Filters.Trandoshan, Filters.atLocation(self))) >= 2) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            action.setActionMsg("Make opponent lose 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerOnDarkSideOfLocation), 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition unlessYouControl = new UnlessCondition(new ControlsCondition(playerOnLightSideOfLocation, self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Jedi, unlessYouControl, 1, Filters.Kashyyyk_location));
        modifiers.add(new MayFireOnlyOncePerBattleModifier(self,
                Filters.and(Filters.lightsaber, Filters.attachedTo(Filters.Jedi), Filters.at(Filters.Kashyyyk_location)), unlessYouControl));
        return modifiers;
    }
}
