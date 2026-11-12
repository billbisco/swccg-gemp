package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Title: Hutt Bounty
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Hutt Bounty]] (Decipher archive scan): deploys on any smuggler, gambler or thief (yours
 * too). The Players Committee errata (Advanced Rulebook Appendix A; supersededOn is an upper bound, the exact date is not on
 * the wiki) made it an opponent's smuggler, gambler or thief and reworded the capture as "captured, seized by
 * a bounty hunter" (the same capture), which the current card (Card6_144) plays; this state differs only in its deploy
 * target.
 */
public class Card6_144o extends Card6_144 {
    public Card6_144o() {
        super();
        setGameText("Deploy on a smuggler, gambler or thief. If subsequently captured by a bounty hunter and then transferred to Jabba's Palace: Dungeon, retrieve Force equal to character's forfeit (+6 if Han) and lose Effect. (Immune to Alter.)");
        setTextState(new TextState("6_144", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(2023, 1, 1)));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        // As printed: any smuggler, gambler or thief, not only the opponent's.
        return Filters.or(Filters.smuggler, Filters.gambler, Filters.thief);
    }
}
