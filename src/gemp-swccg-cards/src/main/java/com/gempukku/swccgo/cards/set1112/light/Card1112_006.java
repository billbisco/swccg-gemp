package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForceGenerationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Title: Crash Site Memorial
 */
public class Card1112_006 extends AbstractNormalEffect {
    public Card1112_006() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Crash_Site_Memorial, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Mos Eisley was built around wreckage of the colony ship Dowager Queen. The wreck remains as a monument where residents leave junk for Jawa scavengers.");
        setGameText("Deploy on table. Dune Sea, Hutt Canyon, Jawa Camp, and Jawa Canyon are deserts. At deserts you occupy with a Jawa, Rontos and Sandcrawlers are power +1 and forfeit +2, and opponent's Force generation is canceled. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter namedDeserts = Filters.or(
                Filters.title(Title.Dune_Sea),
                Filters.title("Tatooine: Hutt Canyon"),
                Filters.title(Title.Jawa_Camp),
                Filters.title("Tatooine: Jawa Canyon"));
        Filter desertsYouOccupyWithJawa = Filters.and(Filters.desert, Filters.occupiesWith(playerId, self, Filters.Jawa));
        Filter rontosAndSandcrawlersThere = Filters.and(Filters.or(Filters.Ronto, Filters.sandcrawler), Filters.at(desertsYouOccupyWithJawa));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, namedDeserts, Keyword.DESERT));
        modifiers.add(new PowerModifier(self, rontosAndSandcrawlersThere, 1));
        modifiers.add(new ForfeitModifier(self, rontosAndSandcrawlersThere, 2));
        modifiers.add(new ResetForceGenerationModifier(self, desertsYouOccupyWithJawa, 0, opponent));
        return modifiers;
    }
}
