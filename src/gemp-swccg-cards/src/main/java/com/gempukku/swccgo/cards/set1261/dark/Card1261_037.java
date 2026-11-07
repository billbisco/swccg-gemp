package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 T'doshok Hunting Vow. */
public class Card1261_037 extends AbstractNormalEffect {
    public Card1261_037() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "T'doshok Hunting Vow", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Rek guza kias n'ek kriesuk zief. Rek res fesruk T'doshok kulesuk luk g'razzurg koref selukra fes k'nel. Kren'ef, T'doshok res luk rek grien forek res fel luraken.");
        setGameText("Deploy on table. Where you have two non-unique aliens present, draw one battle destiny if unable to otherwise and opponent's total power is -3. While Bossk at a site, (except Audience Chamber), Trandoshans are power +1 and forfeit +2. May deploy non-[Virtual Block 6] Molator from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter supportedLocations = new Filter() {
            @Override public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard location) {
                return Filters.location.accepts(state, querying, location)
                        && Filters.canSpot(state.getGame(), self, 2, Filters.and(Filters.your(self), Filters.non_unique,
                        Filters.alien, Filters.presentAt(location)));
            }
        };
        var bosskAtSite = new OnTableCondition(self, Filters.and(Filters.Bossk,
                Filters.at(Filters.and(Filters.site, Filters.not(Filters.Audience_Chamber)))));
        return List.of(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new DuringBattleAtCondition(supportedLocations), 1),
                new TotalPowerModifier(self, supportedLocations, -3, game.getOpponent(self.getOwner())),
                new PowerModifier(self, Filters.species(Species.TRANDOSHAN), bosskAtSite, 1),
                new ForfeitModifier(self, Filters.species(Species.TRANDOSHAN), bosskAtSite, 2));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__TDOSHOK_HUNTING_VOW__DEPLOY_MOLATOR;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, id);
            action.setText("Deploy non-Block-6 Molator from Reserve Deck");
            action.appendEffect(new DeployCardFromReserveDeckEffect(action,
                    Filters.and(Filters.Molator, Filters.not(Icon.LEGACY_BLOCK_6)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
