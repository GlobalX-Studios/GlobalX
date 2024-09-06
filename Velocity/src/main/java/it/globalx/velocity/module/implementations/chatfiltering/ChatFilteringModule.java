package it.globalx.velocity.module.implementations.chatfiltering;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.globalx.velocity.module.Module;
import it.globalx.velocity.module.implementations.chatfiltering.check.implementation.badwords.BadWordsCheck;
import it.globalx.velocity.module.implementations.chatfiltering.check.implementation.badwords.word.BadWord;
import it.globalx.velocity.module.implementations.chatfiltering.check.implementation.badwords.word.action.IntentAction;
import it.globalx.velocity.module.implementations.chatfiltering.check.implementation.domain.DomainCheck;
import it.globalx.velocity.module.implementations.chatfiltering.utils.FilterUtils;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ChatFilteringModule extends Module {
    private BadWordsCheck badWordsCheck;
    private DomainCheck domainCheck;

    public ChatFilteringModule() {
        super("chatfiltering", "Chat filtering with single and multi-word support", "ChatFiltering");
    }

    @Override
    protected void onEnable(Section section) {
        Set<BadWord> badWords = new HashSet<>();

        for (Object wordObject : section.getSection("FilteredWords").getKeys()) {
            String word = (String) wordObject;

            IntentAction intentAction = IntentAction.REPLACE;

            String param = section.getSection("FilteredWords").getString(word);

            if (param.equals("Result.DENIED")) {
                intentAction = IntentAction.BLOCK_MESSAGE;
            }

            badWords.add(new BadWord(word, intentAction, param));
        }

        badWordsCheck = new BadWordsCheck(badWords);
        domainCheck = new DomainCheck();

        new FilterUtils(this);
    }

    @Override
    public void onDisable() {

    }
}
