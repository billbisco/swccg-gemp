package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresObjectiveRestrictionsWhenInitiatingBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5 Plo Koon (V), printing 48. */
public class Card1250_049 extends AbstractJediMaster {
    public Card1250_049() {
        super(Side.LIGHT, 2, 6, 5, 7, 5, "Plo Koon", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Kel Dor Jedi Council member descended from a long line of Jedi. Known for exceptional sensory skills, Plo Koon's control of the Force is unmatched by most.");
        setGameText("Adds 2 to power of anything he pilots. Once per game, may deploy a weapon here from Reserve Deck; reshuffle. While piloting, may battle regardless of objective restrictions. If a battle was just initiated here, may place your weapon here in Used Pile to make all non-vehicle weapon destiny draws -1.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
        addKeyword(Keyword.JEDI_COUNCIL_MEMBER);
        addPersona(Persona.PLO);
        setSpecies(Species.KEL_DOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2),
                new IgnoresObjectiveRestrictionsWhenInitiatingBattleAtLocationModifier(self,
                        Filters.sameLocation(self), new PilotingCondition(self), self.getOwner()));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__PLO_KOON_V__DEPLOY_WEAPON;
        if (!GameConditions.isOncePerGame(game, self, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id, true, false)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, id);
        action.setText("Deploy a weapon here from Reserve Deck");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action,
                Filters.and(Filters.weapon, Filters.canBeTargetedBy(self)),
                Filters.locationAndCardsAtLocation(Filters.sameLocation(self)), 0, true));
        return List.of(action);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.battleInitiatedAt(game, result, Filters.sameLocation(self))) return null;
        Filter canPlace = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard weapon) {
                return !modifiers.mayNotBePlacedInUsedPile(state, weapon, playerId);
            }
        };
        Filter weaponsHere = Filters.and(Filters.your(playerId), Filters.weapon, Filters.here(self), canPlace);
        if (!GameConditions.canSpot(game, self, weaponsHere)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
        action.setText("Place your weapon in Used Pile to reduce non-vehicle weapon destiny");
        action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose your weapon here", weaponsHere) {
            @Override
            protected void cardSelected(PhysicalCard weapon) {
                action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, weapon));
                action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                        new EachWeaponDestinyModifier(self, Filters.not(Filters.vehicle_weapon), -1),
                        "Non-vehicle weapon destiny draws -1"));
            }
        });
        return List.of(action);
    }
}
