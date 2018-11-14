package akka.persistence.ignite.common.entities;

import org.apache.ignite.IgniteCache;

import akka.persistence.ignite.common.JournalCacheProvider;
import akka.persistence.ignite.journal.JournalItem;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * the wrapper for journal and sequence cache to be used by  {@link JournalCacheProvider}
 */
@Builder
@Getter
@ToString
public class JournalCaches {
    private IgniteCache<Long, JournalItem> journalCache;
    private IgniteCache<String, Long> sequenceCache;
}
