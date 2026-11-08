package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Device
 * Title: Trophy Of A Kill
 * Pre-errata version of 1273_11 (Virtual Block 7.3 release sheet text, Jun 2011), replaced by the Virtual Cards Master List text (Cyborg Commander renamed Grievous). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_011 extends AbstractCharacterDevice {
    public Card2273_011() {
        super(Side.DARK, 6, "Trophy Of A Kill", Uniqueness.RESTRICTED_2, ExpansionSet.LEGACY, Rarity.V);
        setGameText("Deploy on Cyborg Commander, Galen, or your female of ability > 3. Character is immunity to attrition +2 (and power +2 if female). May add 1 to Force drain where present. This device lost if character Disarmed.");
        setLegacyVersion(new LegacyVersion("1273_11", "",
                "Virtual Block 7.3 release sheet (2011-06-03)", LocalDate.of(2011, 6, 3), LocalDate.of(2013, 4, 27)));
        addIcons(Icon.PREMIUM, Icon.LEGACY_BLOCK_7);
        setAsLegacy(true);
    }

    /** Cyborg Commander: Grievous, Hunter Of Jedi's printed title on the Virtual Block 7.1 sheet (that card only, not other Grievous cards). */
    static final Filter CYBORG_COMMANDER = Filters.title("Grievous, Hunter Of Jedi");

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(CYBORG_COMMANDER, Filters.Galen, Filters.and(Filters.female, Filters.abilityMoreThan(3))));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(CYBORG_COMMANDER, Filters.Galen, Filters.and(Filters.female, Filters.abilityMoreThan(3)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(hasAttached, Filters.female), 2));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, hasAttached, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self))
                && GameConditions.canUseDevice(game, self.getAttachedTo(), self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            // Perform result(s)
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.justDisarmed(game, effectResult, Filters.hasAttached(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}
