package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set1281.light.Card1281_032;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Imperial Navigation Charts
 * Original version of 1281_32 (Virtual Block 8.1 release sheet): the "'steal' this Effect" is not optional (the PC website card list, 2012-07-21, PC-E1, says "may 'steal'"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_032 extends Card1281_032 {
    public Card2281_032() {
        super();
        setGameText("Deploy on a Nar Shaddaa battleground site. If you control three Nar Shaddaa locations or just won a battle (or initiated a Force drain) here, 'steal' this Effect (relocate to your side of table). Place in Used Pile if opponent controls three Nar Shaddaa locations. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_32", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }

    /** The release sheet's steal is not optional ("'steal' this Effect"): no player choice. */
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        List<RequiredGameTextTriggerAction> placeInUsedPile = super.getGameTextRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
        if (placeInUsedPile != null)
            actions.addAll(placeInUsedPile);

        String playerId = self.getOwner();
        Filter here = Filters.here(self);
        if (!alreadyOnYourSideOfTable(self, playerId)
                && ((TriggerConditions.isTableChanged(game, effectResult) && GameConditions.controls(game, playerId, 3, Filters.partOfSystem(Title.Nar_Shaddaa)))
                || TriggerConditions.wonBattleAt(game, effectResult, playerId, here)
                || TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, here))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("'Steal' Imperial Navigation Charts");
            action.appendEffect(stealToSideOfTable(action, self, playerId));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }
}
