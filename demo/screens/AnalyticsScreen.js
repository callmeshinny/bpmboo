import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  ActivityIndicator,
  TouchableOpacity,
} from 'react-native';
import { heartRateAPI } from '../utils/api';

export default function AnalyticsScreen() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [period, setPeriod] = useState('week');

  useEffect(() => {
    fetchStats();
  }, [period]);

  const fetchStats = async () => {
    setLoading(true);
    try {
      const response = await heartRateAPI.getStats(period);
      setStats(response.data);
    } catch (error) {
      console.error('Error fetching stats:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#e74c3c" />
      </View>
    );
  }

  const StatCard = ({ label, value, unit = '' }) => (
    <View style={styles.statCard}>
      <Text style={styles.statLabel}>{label}</Text>
      <Text style={styles.statValue}>
        {value || '-'} {unit}
      </Text>
    </View>
  );

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Analytics</Text>

      <View style={styles.periodButtons}>
        {['day', 'week', 'month'].map((p) => (
          <TouchableOpacity
            key={p}
            style={[styles.periodBtn, period === p && styles.periodBtnActive]}
            onPress={() => setPeriod(p)}
          >
            <Text style={[styles.periodText, period === p && styles.periodTextActive]}>
              {p.charAt(0).toUpperCase() + p.slice(1)}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      <View style={styles.statsGrid}>
        <StatCard label="Average" value={stats?.average?.toFixed(0)} unit="BPM" />
        <StatCard label="Max" value={stats?.max} unit="BPM" />
        <StatCard label="Min" value={stats?.min} unit="BPM" />
        <StatCard label="Count" value={stats?.count} unit="records" />
      </View>

      {stats?.insights && (
        <View style={styles.insightCard}>
          <Text style={styles.insightTitle}>Insights</Text>
          <Text style={styles.insightText}>{stats.insights}</Text>
        </View>
      )}

      <View style={styles.rangeCard}>
        <Text style={styles.rangeTitle}>Heart Rate Ranges</Text>
        <View style={styles.rangeItem}>
          <View style={[styles.rangeBadge, { backgroundColor: '#27ae60' }]} />
          <Text style={styles.rangeText}>Normal: 60-100 BPM</Text>
        </View>
        <View style={styles.rangeItem}>
          <View style={[styles.rangeBadge, { backgroundColor: '#f39c12' }]} />
          <Text style={styles.rangeText}>Low: &lt; 60 BPM</Text>
        </View>
        <View style={styles.rangeItem}>
          <View style={[styles.rangeBadge, { backgroundColor: '#e74c3c' }]} />
          <Text style={styles.rangeText}>High: &gt; 100 BPM</Text>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 15,
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  periodButtons: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginBottom: 20,
  },
  periodBtn: {
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 5,
    backgroundColor: '#f0f0f0',
  },
  periodBtnActive: {
    backgroundColor: '#e74c3c',
  },
  periodText: {
    color: '#666',
    fontWeight: 'bold',
  },
  periodTextActive: {
    color: '#fff',
  },
  statsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    marginBottom: 20,
  },
  statCard: {
    width: '48%',
    backgroundColor: '#f5f5f5',
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
  },
  statLabel: {
    fontSize: 12,
    color: '#999',
    marginBottom: 5,
  },
  statValue: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#e74c3c',
  },
  insightCard: {
    backgroundColor: '#ecf0f1',
    padding: 15,
    borderRadius: 8,
    marginBottom: 20,
  },
  insightTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  insightText: {
    fontSize: 13,
    color: '#555',
    lineHeight: 20,
  },
  rangeCard: {
    backgroundColor: '#f5f5f5',
    padding: 15,
    borderRadius: 8,
    marginBottom: 20,
  },
  rangeTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  rangeItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  rangeBadge: {
    width: 12,
    height: 12,
    borderRadius: 6,
    marginRight: 10,
  },
  rangeText: {
    fontSize: 13,
    color: '#555',
  },
});
