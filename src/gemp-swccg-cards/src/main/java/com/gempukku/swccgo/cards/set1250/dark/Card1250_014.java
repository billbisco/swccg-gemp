package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.set1116.dark.Card1116_044;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 5: Expand The Empire (V). */
public class Card1250_014 extends Card1116_044 {
    public Card1250_014() {
        setLore("The Emperor disbanded the Imperial Senate 'for the duration of the emergency,' seizing absolute power. He planned to extend rule by terrorizing planets into submission.");
        setGameText("Deploy on Senate. May raise your converted Senate to the top. Jedi here are politics +4. While your total politics here is > 5, at battlegrounds where you have an Imperial, your Force drains and battle destiny draws are +1. Your Maul, aliens (except Mara), [Independent Starship] starships, and senators are lost. (Immune to Alter.)");
        addIcon(Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
                                                         PlayCardOptionId option, boolean asReact) {
        return Filters.Galactic_Senate;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        var senate = self.getAttachedTo();
        if (senate == null || game.getGameState().getConvertedLocationsUnderTopLocation(senate).stream()
                .noneMatch(location -> playerId.equals(location.getOwner()))) return null;
        var action = new TopLevelGameTextAction(self, sourceId);
        action.setText("Raise your converted Senate to the top");
        action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, senate, true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String owner = self.getOwner();
        Condition politics = (state, querying) -> querying.getTotalPoliticsAtGalacticSenate(state, owner) > 5;
        Filter battlegrounds = Filters.and(Filters.battleground,
                Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Imperial)));
        var modifiers = new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.add(new ForceDrainModifier(self, battlegrounds, politics, 1, owner));
        modifiers.add(new EachBattleDestinyModifier(self, battlegrounds, politics, 1, owner));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                   PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result)) return null;
        Filter loses = Filters.and(Filters.your(self), Filters.or(Filters.Maul,
                Filters.and(Filters.alien, Filters.not(Filters.Mara_Jade)),
                Filters.and(Filters.starship, Icon.INDEPENDENT), Filters.senator));
        var cards = Filters.filterAllOnTable(game, loses);
        if (cards.isEmpty()) return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Lose your Maul, other aliens, Independent starships, and senators");
        action.appendEffect(new LoseCardsFromTableEffect(action, cards, true));
        return List.of(action);
    }
}
