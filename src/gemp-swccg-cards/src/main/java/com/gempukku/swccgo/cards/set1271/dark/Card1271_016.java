package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Vader's Helmet, Virtual Block 7.1. */
public class Card1271_016 extends LegacyVaderEquipment {
    public Card1271_016() {
        super(0, "Vader's Helmet");
        setLore("'If once you start down the dark path, forever will it dominate your destiny. Consume you it will, as it did Obi-Wan's apprentice.'");
        setGameText("Deploy on [Vader] Vader. When Vader moves to same site as a Jedi or Skywalker, opponent loses 1 Force. If Obi-Wan's Journal here, may place it on bottom of opponent's Lost Pile. While with opponent's character of ability > 4, Vader is power +1 and draws two battle destiny if not able to otherwise. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter wearer = Filters.hasAttached(self);
        Condition withHighAbilityOpponent = (state, modifiers) -> {
            PhysicalCard vader = self.getAttachedTo();
            return vader != null && GameConditions.canSpot(game, self, Filters.and(
                Filters.opponents(self), Filters.character, Filters.abilityMoreThan(4), Filters.presentWith(vader)));
        };
        return List.of(new PowerModifier(self, wearer, withHighAbilityOpponent, 1),
            new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, wearer, withHighAbilityOpponent, 2));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        PhysicalCard vader = self.getAttachedTo();
        if (vader == null) return null;
        Filter jediOrSkywalker = Filters.and(Filters.or(Filters.Jedi, Filters.Skywalker), Filters.except(vader));
        if (!TriggerConditions.movedToLocation(game, result, vader, Filters.and(Filters.site, Filters.sameSiteAs(self, jediOrSkywalker)))) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Make opponent lose 1 Force");
        action.appendEffect(new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        Filter journal = Filters.and(Filters.title(Title.ObiWans_Journal), Filters.opponents(self), Filters.atSameLocation(self));
        if (!GameConditions.canSpot(game, self, journal)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId);
        action.setText("Place Obi-Wan's Journal on bottom of opponent's Lost Pile");
        action.appendEffect(new ChooseCardOnTableEffect(action, playerId, "Choose Obi-Wan's Journal", journal) {
            @Override protected void cardSelected(PhysicalCard selected) {
                action.appendEffect(new LoseCardFromTableEffect(action, selected, false, true));
            }
        });
        return List.of(action);
    }
}
