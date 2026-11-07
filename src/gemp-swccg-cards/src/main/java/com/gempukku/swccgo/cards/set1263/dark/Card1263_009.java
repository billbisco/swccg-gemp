package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToCancelModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Legacy Block 6.3: Guild Of Assassins. */
public class Card1263_009 extends AbstractNormalEffect {
    private static final Map<String, Filter> ASSASSINS = Map.of(
            "Aurra", Filters.Aurra, "Galen", Filters.Galen, "Guri", Filters.Guri, "Mara", Filters.Mara_Jade);
    private static final Filter ASSASSIN = Filters.or(ASSASSINS.values().toArray(Filter[]::new));
    private static final Filter ASSASSIN_SITE = Filters.or(Filters.Coruscant_site, Filters.Tatooine_site);

    public Card1263_009() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Guild Of Assassins", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("Greedo cheskopokuta klees ruya Solo. Hoko yanee boopa gush Cantina. Cheeco wa Solo's anye nyuma Greedo vakee. Jabba kul steeka et en anpaw.");
        setGameText("Deploy on table. Maul is lost. Once per character, when you deploy Aurra, Galen, Guri, or Mara to a Coruscant or Tatooine site, may take any one card from Used Pile into hand; reshuffle. At locations you occupy, your Force generation may not be prevented or canceled. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                   PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result)) return null;
        var maulCards = Filters.filterAllOnTable(game, Filters.Maul);
        if (maulCards.isEmpty()) return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Lose Maul");
        action.appendEffect(new LoseCardsFromTableEffect(action, maulCards, true));
        return List.of(action);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
                                                                                  EffectResult result, PhysicalCard self,
                                                                                  int sourceId) {
        var id = GameTextActionId.LEGACY__GUILD_OF_ASSASSINS__TAKE_FROM_USED_PILE;
        if (!TriggerConditions.justDeployedTo(game, result, playerId, Filters.and(Filters.character, ASSASSIN),
                ASSASSIN_SITE) || !GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, id)) return null;

        var deployed = ((PlayCardResult) result).getPlayedCard();
        if (!GameConditions.canSpot(game, self, deployed)) return null;
        Set<String> used = self.getWhileInPlayData() == null ? Set.of() : self.getWhileInPlayData().getTextValues();
        var names = ASSASSINS.entrySet().stream().filter(assassin -> !used.contains(assassin.getKey())
                && assassin.getValue().accepts(game, deployed)).map(Map.Entry::getKey).collect(Collectors.toSet());
        if (names.isEmpty()) return null;

        var action = new OptionalGameTextTriggerAction(self, sourceId, id);
        action.setText("Take any card into hand from Used Pile");
        action.appendUsage(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                if (self.getWhileInPlayData() == null) self.setWhileInPlayData(new WhileInPlayData(new HashSet<String>()));
                self.getWhileInPlayData().getTextValues().addAll(names);
            }
        });
        action.appendEffect(new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ForceGenerationImmuneToCancelModifier(self, Filters.occupies(self.getOwner()),
                null, Filters.any, self.getOwner()));
    }
}
