import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { authAPI, otpAPI } from '../utils/api';

export default function LoginScreen({ navigation, onAuthChange }) {
  const [email, setEmail] = useState('demo@gmail.com');
  const [password, setPassword] = useState('123456');
  const [loading, setLoading] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [registerData, setRegisterData] = useState({
    email: '',
    password: '',
    fullName: '',
    phone: '',
    dob: '',
    gender: '',
    emergencyName: '',
    emergencyPhone: '',
  });

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert('Error', 'Please fill in all fields');
      return;
    }

    setLoading(true);
    try {
      const response = await authAPI.login({ email, password });
      const { token, user } = response.data;

      await AsyncStorage.setItem('token', token);
      await AsyncStorage.setItem('userId', user._id);
      await AsyncStorage.setItem('userData', JSON.stringify(user));

      if (onAuthChange) onAuthChange(true);
      navigation.replace('MainTabs');
    } catch (error) {
      Alert.alert('Login Failed', error.response?.data?.message || 'Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async () => {
    if (!registerData.email || !registerData.password || !registerData.fullName) {
      Alert.alert('Error', 'Please fill in required fields');
      return;
    }

    setLoading(true);
    try {
      const response = await authAPI.register(registerData);
      const { token, user } = response.data;

      await AsyncStorage.setItem('token', token);
      await AsyncStorage.setItem('userId', user._id);
      await AsyncStorage.setItem('userData', JSON.stringify(user));

      if (onAuthChange) onAuthChange(true);
      setShowRegister(false);
      navigation.replace('MainTabs');
    } catch (error) {
      Alert.alert('Registration Failed', error.response?.data?.message || 'Registration error');
    } finally {
      setLoading(false);
    }
  };

  const handleRequestOtp = async () => {
    if (!email) {
      Alert.alert('Error', 'Please enter your email');
      return;
    }

    setLoading(true);
    try {
      await otpAPI.request({ email });
      Alert.alert('Success', 'OTP sent to your email');
      navigation.navigate('Otp', { email });
    } catch (error) {
      Alert.alert('Error', error.response?.data?.message || 'Failed to send OTP');
    } finally {
      setLoading(false);
    }
  };

  if (showRegister) {
    return (
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.container}>
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <Text style={styles.title}>Create Account</Text>

          <TextInput
            style={styles.input}
            placeholder="Email"
            value={registerData.email}
            onChangeText={(text) => setRegisterData({ ...registerData, email: text })}
            keyboardType="email-address"
          />

          <TextInput
            style={styles.input}
            placeholder="Password"
            value={registerData.password}
            onChangeText={(text) => setRegisterData({ ...registerData, password: text })}
            secureTextEntry
          />

          <TextInput
            style={styles.input}
            placeholder="Full Name"
            value={registerData.fullName}
            onChangeText={(text) => setRegisterData({ ...registerData, fullName: text })}
          />

          <TextInput
            style={styles.input}
            placeholder="Phone"
            value={registerData.phone}
            onChangeText={(text) => setRegisterData({ ...registerData, phone: text })}
          />

          <TextInput
            style={styles.input}
            placeholder="Date of Birth (DD/MM/YYYY)"
            value={registerData.dob}
            onChangeText={(text) => setRegisterData({ ...registerData, dob: text })}
          />

          <TextInput
            style={styles.input}
            placeholder="Emergency Contact Name"
            value={registerData.emergencyName}
            onChangeText={(text) => setRegisterData({ ...registerData, emergencyName: text })}
          />

          <TextInput
            style={styles.input}
            placeholder="Emergency Phone"
            value={registerData.emergencyPhone}
            onChangeText={(text) => setRegisterData({ ...registerData, emergencyPhone: text })}
          />

          <TouchableOpacity
            style={[styles.button, loading && styles.buttonDisabled]}
            onPress={handleRegister}
            disabled={loading}
          >
            <Text style={styles.buttonText}>{loading ? 'Creating...' : 'Register'}</Text>
          </TouchableOpacity>

          <TouchableOpacity onPress={() => setShowRegister(false)}>
            <Text style={styles.linkText}>Already have an account? Sign In</Text>
          </TouchableOpacity>
        </ScrollView>
      </KeyboardAvoidingView>
    );
  }

  return (
    <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Text style={styles.title}>BPMBoo</Text>
        <Text style={styles.subtitle}>Heart Rate Monitor</Text>

        <TextInput
          style={styles.input}
          placeholder="Email"
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          editable={!loading}
        />

        <TextInput
          style={styles.input}
          placeholder="Password"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
          editable={!loading}
        />

        <TouchableOpacity
          style={[styles.button, loading && styles.buttonDisabled]}
          onPress={handleLogin}
          disabled={loading}
        >
          <Text style={styles.buttonText}>{loading ? 'Signing in...' : 'Sign In'}</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.buttonOtp, loading && styles.buttonDisabled]}
          onPress={handleRequestOtp}
          disabled={loading}
        >
          <Text style={styles.buttonText}>Sign In with OTP</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => setShowRegister(true)}>
          <Text style={styles.linkText}>Don't have an account? Register</Text>
        </TouchableOpacity>

        <Text style={styles.demoText}>Demo: demo@gmail.com / 123456</Text>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 10,
    color: '#e74c3c',
  },
  subtitle: {
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 40,
    color: '#666',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 12,
    marginBottom: 15,
    borderRadius: 8,
    fontSize: 16,
  },
  button: {
    backgroundColor: '#e74c3c',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 10,
  },
  buttonOtp: {
    backgroundColor: '#27ae60',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 10,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  linkText: {
    textAlign: 'center',
    color: '#e74c3c',
    marginTop: 15,
    fontSize: 14,
  },
  demoText: {
    textAlign: 'center',
    color: '#999',
    marginTop: 30,
    fontSize: 12,
  },
});
