package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: WED-9-M1 'Bantha' Droid
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[WED-9-M1 'Bantha' Droid]]. As printed its starfighter immunity
 * applies only at the related system. The errata ([[WED-9-M1 'Bantha' Droid (Errata)]], Decipher Glossary Version 2.0
 * (November 1998)) added sectors.
 * Copy of Card1_032 with "system or sector" narrowed to "system"; everything else plays as Card1_032.
 */
public class Card1_032o extends AbstractDroid {
    public Card1_032o() {
        super(Side.LIGHT, 4, 2, 1, 3, "WED-9-M1 'Bantha' Droid", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Unique treadwell droid cobbled together by Jawas. Now owned by the DeMaals, proprietors of Docking Bay 94. Nicknamed 'bantha' for its slow and stubborn personality.");
        setGameText("All your vehicles and droids at same location are immune to attrition < 2. Also, if 'bantha' droid is at a docking bay, all your starfighters at the related system are immune to attrition < 3 and may cancel Lateral Damage.");
        setTextState(new TextState("1_32", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
        addModelType(ModelType.MAINTENANCE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.vehicle,
                Filters.droid), Filters.at(Filters.sameLocation(self))), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.starfighter,
                Filters.at(Filters.relatedSystem(self))), new AtCondition(self, Filters.docking_bay), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.docking_bay)
                && GameConditions.canTargetToCancel(game, self, Filters.Lateral_Damage)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Lateral_Damage, Title.Lateral_Damage);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Lateral_Damage)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isAtLocation(game, self, Filters.docking_bay)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
