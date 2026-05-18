import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { heartRateAPI } from '../utils/api';

export default function MeasureScreen({ navigation }) {
  const [hr, setHr] = useState(null);
  const [loading, setLoading] = useState(false);
  const [measuring, setMeasuring] = useState(false);

  const handleMeasure = async () => {
    setMeasuring(true);
    
    // Simulate heart rate measurement
    setTimeout(() => {
      const randomHr = Math.floor(Math.random() * (100 - 60) + 60);
      setHr(randomHr);
      setMeasuring(false);
    }, 2000);
  };

  const handleSave = async () => {
    if (!hr) {
      Alert.alert('Error', 'Please measure first');
      return;
    }

    setLoading(true);
    try {
      await heartRateAPI.create({
        heartRate: hr,
        timestamp: new Date(),
      });
      Alert.alert('Success', 'Heart rate recorded');
      setHr(null);
      navigation.navigate('History');
    } catch (error) {
      Alert.alert('Error', error.response?.data?.message || 'Failed to save');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Heart Rate Measurement</Text>

      <View style={styles.card}>
        {measuring ? (
          <View style={styles.centerContent}>
            <ActivityIndicator size="large" color="#e74c3c" />
            <Text style={styles.measuringText}>Measuring...</Text>
          </View>
        ) : (
          <>
            <Text style={styles.hrValue}>{hr || '--'}</Text>
            <Text style={styles.hrLabel}>BPM</Text>
          </>
        )}
      </View>

      <TouchableOpacity
        style={[styles.button, measuring && styles.buttonDisabled]}
        onPress={handleMeasure}
        disabled={measuring}
      >
        <Text style={styles.buttonText}>{measuring ? 'Measuring...' : 'Start Measurement'}</Text>
      </TouchableOpacity>

      {hr && (
        <TouchableOpacity
          style={[styles.buttonSave, loading && styles.buttonDisabled]}
          onPress={handleSave}
          disabled={loading}
        >
          <Text style={styles.buttonText}>{loading ? 'Saving...' : 'Save Record'}</Text>
        </TouchableOpacity>
      )}

      <View style={styles.info}>
        <Text style={styles.infoTitle}>Normal Range</Text>
        <Text style={styles.infoText}>60-100 BPM (resting)</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 20,
    justifyContent: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 30,
    textAlign: 'center',
  },
  card: {
    backgroundColor: '#f5f5f5',
    borderRadius: 15,
    padding: 40,
    marginBottom: 30,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 200,
  },
  centerContent: {
    alignItems: 'center',
  },
  hrValue: {
    fontSize: 72,
    fontWeight: 'bold',
    color: '#e74c3c',
  },
  hrLabel: {
    fontSize: 18,
    color: '#666',
    marginTop: 10,
  },
  measuringText: {
    marginTop: 15,
    fontSize: 16,
    color: '#666',
  },
  button: {
    backgroundColor: '#e74c3c',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 10,
  },
  buttonSave: {
    backgroundColor: '#27ae60',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 10,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  info: {
    backgroundColor: '#ecf0f1',
    padding: 15,
    borderRadius: 8,
    marginTop: 20,
  },
  infoTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  infoText: {
    fontSize: 13,
    color: '#666',
  },
});
