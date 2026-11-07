package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Death Star: Central Core (V), Virtual Block 7.1. */
public class Card1271_022 extends AbstractSite {
    private static final Filter BLOWN_AWAY_SYSTEM = Filters.and(Filters.system, new Filter() {
        @Override public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
            return card.isBlownAway();
        }
    });

    public Card1271_022() {
        super(Side.DARK, Title.Death_Star_Central_Core, Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("May deploy Superlaser from Reserve Deck; reshuffle. At 'blown away' systems, adds one [Light Side] and one [Dark Side]. Whenever you Force drain at a 'blown away' system Death Star orbits, may retrieve 1 Force.");
        setLocationLightSideGameText("Opponent's Force drain bonuses at systems (except your systems) are limited to 1. Immune to Expand The Empire.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String darkPlayer, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__CENTRAL_CORE__DEPLOY_SUPERLASER;
        if (!GameConditions.canDeployCardFromReserveDeck(game, darkPlayer, self, id, Title.Superlaser)
                || !GameConditions.canSpotLocation(game, Filters.Death_Star_system)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, darkPlayer, sourceId, id);
        action.setText("Deploy Superlaser from Reserve Deck");
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Superlaser, true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String darkPlayer, SwccgGame game, PhysicalCard self) {
        return List.of(new IconModifier(self, BLOWN_AWAY_SYSTEM, Icon.LIGHT_FORCE, 1),
            new IconModifier(self, BLOWN_AWAY_SYSTEM, Icon.DARK_FORCE, 1));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String lightPlayer, SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToTitleModifier(self, Title.Expand_The_Empire));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String darkPlayer, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        Filter orbitedWreck = Filters.and(BLOWN_AWAY_SYSTEM, new Filter() {
            @Override public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard wreck) {
                // The generic isOrbitedBy filter omits blown-away titles.
                return Filters.findFirstFromTopLocationsOnTable(game,
                    Filters.and(Filters.Death_Star_system, Filters.isOrbiting(wreck.getTitle()))) != null;
            }
        });
        if (!TriggerConditions.forceDrainInitiatedBy(game, result, darkPlayer, orbitedWreck)
                || !GameConditions.hasLostPile(game, darkPlayer)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, darkPlayer, sourceId);
        action.setText("Retrieve 1 Force at the blown-away system");
        action.appendEffect(new RetrieveForceEffect(action, darkPlayer, 1));
        return List.of(action);
    }
}
