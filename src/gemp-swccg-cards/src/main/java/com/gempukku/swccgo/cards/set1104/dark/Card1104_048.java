package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Starship
 * Subtype: Capital
 * Title: Stalker
 */
public class Card1104_048 extends AbstractCapitalStarship {
    public Card1104_048() {
        super(Side.DARK, 1, 8, 8, 7, null, 3, 9, Title.Stalker, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Originally assigned to search the Outer Rim for new worlds to subjugate. Launched the probe droid that found Echo Base. Later reassigned to Death Squadron.");
        setGameText("May add 6 pilots, 8 passengers, 2 vehicles and 4 TIEs. Permanent pilot provides ability of 2. During your deploy phase, may ▼ one [Virtual] Probe Droid to a related site.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(2);
        setTIECapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__STALKER__DEPLOY_PROBE_DROID;

        Filter virtualProbeDroid = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint() != null
                        && physicalCard.getBlueprint().hasVirtualSuffix()
                        && Filters.probe_droid.accepts(gameState, modifiersQuerying, physicalCard);
            }
        };

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy [Virtual] Probe Droid from Reserve Deck");
            action.setActionMsg("Deploy a [Virtual] Probe Droid to a related site from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action,
                            virtualProbeDroid,
                            Filters.relatedSite(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
