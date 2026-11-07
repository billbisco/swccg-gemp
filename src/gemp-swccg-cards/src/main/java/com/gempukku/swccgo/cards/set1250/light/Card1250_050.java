package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractAlienRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 5 Senator Jar Jar Binks, printing 50. */
public class Card1250_050 extends AbstractAlienRepublic {
    public Card1250_050() {
        super(Side.LIGHT, 2, 2, 3, 3, 4, "Senator Jar Jar Binks", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setPolitics(2);
        setSpecies(Species.GUNGAN);
        addPersona(Persona.JAR_JAR);
        setLore("Gungan.");
        setGameText("Agendas: justice, order. At same and related sites, vehicle weapon destiny draws are -2. Once per turn, may deploy an electropole, a [Coruscant] Naboo location, or a [Theed Palace] vehicle from Reserve Deck; reshuffle. If at Senate, may place Jar Jar and a [Coruscant] Political Effect out of play.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AgendaModifier(self, Agenda.JUSTICE, Agenda.ORDER));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new EachWeaponDestinyModifier(self, Filters.vehicle_weapon,
                Filters.at(Filters.sameOrRelatedSite(self)), -2));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        GameTextActionId id = GameTextActionId.LEGACY__SENATOR_JAR_JAR_BINKS__DEPLOY;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            Filter card = Filters.and(Filters.canBeTargetedBy(self), Filters.or(Filters.title("Electropole"),
                    Filters.and(Icon.CORUSCANT, Filters.Naboo_location),
                    Filters.and(Icon.THEED_PALACE, Filters.vehicle)));
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, id);
            action.setText("Deploy an electropole, Coruscant Naboo location, or Theed Palace vehicle");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, card, true));
            actions.add(action);
        }

        Filter canPlaceOutOfPlay = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
                return !modifiers.mayNotBePlacedOutOfPlay(state, card);
            }
        };
        Filter political = Filters.and(Filters.Political_Effect, Icon.CORUSCANT, Filters.canBeTargetedBy(self), canPlaceOutOfPlay);
        if (GameConditions.isAtLocation(game, self, Filters.Galactic_Senate)
                && GameConditions.canBePlacedOutOfPlay(game, self) && GameConditions.canTarget(game, self, political)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Place Jar Jar and a Coruscant Political Effect out of play");
            action.appendEffect(new ChooseCardOnTableEffect(action, playerId, "Choose a Coruscant Political Effect", political) {
                @Override
                protected void cardSelected(PhysicalCard card) {
                    action.appendEffect(new PlaceCardsOutOfPlayFromTableEffect(action, List.of(self, card)));
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
